package top.wxyin.share.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.wxyin.share.app.common.cache.RequestContext;
import top.wxyin.share.app.common.cache.TokenStoreCache;
import top.wxyin.share.app.common.result.PageResult;
import top.wxyin.share.app.convert.ResourceConvert;
import top.wxyin.share.app.enums.ResourceStatusEnum;
import top.wxyin.share.app.enums.UserActionEnum;
import top.wxyin.share.app.mapper.ResourceMapper;
import top.wxyin.share.app.mapper.UserMapper;
import top.wxyin.share.app.model.dto.ResourcePublishDTO;
import top.wxyin.share.app.model.entity.Resource;
import top.wxyin.share.app.model.entity.User;
import top.wxyin.share.app.model.query.ResourceQuery;
import top.wxyin.share.app.model.query.UserActionResourceQuery;
import top.wxyin.share.app.model.vo.ResourceDetailVO;
import top.wxyin.share.app.model.vo.ResourceItemVO;
import top.wxyin.share.app.model.vo.UserActionListInfo;
import top.wxyin.share.app.service.CategoryService;
import top.wxyin.share.app.service.ResourceService;
import top.wxyin.share.app.service.TagService;
import top.wxyin.share.app.service.UserActionService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static top.wxyin.share.app.common.constant.Constant.NO_TOKEN;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    private final UserActionService userActionService;
    private final UserMapper userMapper;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final TokenStoreCache tokenStoreCache;

    @Override
    public PageResult<ResourceItemVO> getUserActionResourcePage(UserActionResourceQuery query) {
        Integer userId = RequestContext.getUserId();
        UserActionListInfo userActionListInfo = userActionService
                .selectResourceListByUserIdAndType(userId, UserActionEnum.getByCode(query.getType()), new Page<>(query.getPage(), query.getLimit()))
                ;
        //查询结果为空，直接返回空
        if (userActionListInfo.getTotal() == 0) {
            return new PageResult<>(Collections.emptyList(), 0);
        }
        List<Resource> records = baseMapper.selectBatchIds(userActionListInfo.getResourceIdList());
        List<ResourceItemVO> voList = records.stream().map(resource -> {
            ResourceItemVO vo = new ResourceItemVO();
            vo.setPkId(resource.getPkId());
            vo.setTitle(resource.getTitle());
            vo.setPrice(resource.getPrice());
            vo.setIsTop(resource.getIsTop());
            vo.setDetail(resource.getDetail());
            vo.setStatus(resource.getStatus());
            vo.setCreateTime(resource.getCreateTime());

            User author = userMapper.selectById(resource.getAuthor());
            vo.setAuthor(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
            vo.setDiskType(categoryService.getById(resource.getDiskType()).getTitle());
            vo.setResType(categoryService.queryCategoryNameList(resource.getResType()));
            vo.setTags(tagService.queryTagNamesByIds(resource.getTags()));
            vo.setLikeNum(userActionService.selectCountByResourceIdAndType(resource.getPkId(), UserActionEnum.LIKE));
            vo.setDownloadNum(userActionService.selectCountByResourceIdAndType(resource.getPkId(), UserActionEnum.EXCHANGE));
            vo.setCollectNum(userActionService.selectCountByResourceIdAndType(resource.getPkId(), UserActionEnum.COLLECT));
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(voList, userActionListInfo.getTotal());
    }

    @Override
    public PageResult<ResourceItemVO> getResourcePage(ResourceQuery query) {
        //构造查询条件
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getStatus, ResourceStatusEnum.AUDITED.getCode())
                .like(StringUtils.isNotBlank(query.getKeyword()), Resource::getTitle, "%" + query.getKeyword() + "%")
                .like(StringUtils.isNotBlank(query.getKeyword()), Resource::getDetail, "%" + query.getKeyword() + "%")
                .eq(query.getDiskType() != null && query.getDiskType() > 0, Resource::getDiskType, query.getDiskType())
                .apply(query.getResType() != null && query.getResType() > 0,
                        "JSON_CONTAINS(res_type,JSON_ARRAY({0}))=1", query.getResType())
                .apply(query.getTagId() != null && query.getTagId() > 0, "JSON_CONTAINS(tags,JSON_ARRAY({0}))=1", query.getTagId())
                                .orderByDesc(Resource::getIsTop)
                                .orderByDesc(Resource::getCreateTime);
        //分⻚查询
        Page<Resource> page = page(new Page<>(query.getPage(), query.getLimit()), wrapper);
        List<Resource> records = page.getRecords();
        //构造vo返回集合
        List<ResourceItemVO> voList = records.stream().map(resource -> {
            ResourceItemVO vo = new ResourceItemVO();
            vo.setPkId(resource.getPkId());
            vo.setTitle(resource.getTitle());
            vo.setPrice(resource.getPrice());
            vo.setIsTop(resource.getIsTop());
            vo.setDetail(resource.getDetail());
            vo.setCreateTime(resource.getCreateTime());
            //调⽤⽤户服务，获取⽤户信息
            User author = userMapper.selectById(resource.getAuthor());
            vo.setAuthor(author.getNickname());
            vo.setAuthorAvatar(author.getAvatar());
            //调⽤其他服务，构造参数
            vo.setDiskType(categoryService.getById(resource.getDiskType()).getTitle());
            vo.setResType(categoryService.queryCategoryNameList(resource.getResType()));
            vo.setTags(tagService.queryTagNamesByIds(resource.getTags()));
            vo.setLikeNum(userActionService.selectCountByResourceIdAndType(resource.getPkId(), UserActionEnum.LIKE));
            vo.setDownloadNum(userActionService.selectCountByResourceIdAndType
                    (resource.getPkId(), UserActionEnum.EXCHANGE));
            vo.setCollectNum(userActionService.selectCountByResourceIdAndType(
                    resource.getPkId(), UserActionEnum.COLLECT));
            return vo;
        }).collect(Collectors.toList());
        return new PageResult<>(voList, page.getTotal());
    }

    @Override
    public ResourceDetailVO getResourceDetail(Integer resourceId, String accessToken) {
        Resource resource = getById(resourceId);
        ResourceDetailVO detail = new ResourceDetailVO();
        detail.setPkId(resource.getPkId());
        detail.setTitle(resource.getTitle());
        detail.setPrice(resource.getPrice());
        detail.setIsTop(resource.getIsTop());
        detail.setDownloadUrl(resource.getDownloadUrl());
        detail.setDetail(resource.getDetail());
        detail.setRemark(resource.getRemark());
        detail.setStatus(resource.getStatus());
        detail.setCreateTime(resource.getCreateTime());

        User author = userMapper.selectById(resource.getAuthor());
        detail.setAuthor(author.getNickname());
        detail.setAuthorAvatar(author.getAvatar());
        detail.setDiskType(categoryService.getById(resource.getDiskType()).getTitle());
        detail.setResType(categoryService.queryCategoryNameList(resource.getResType()));
        detail.setTags(tagService.queryTagNamesByIds(resource.getTags()));
        detail.setLikeNum(userActionService.selectCountByResourceIdAndType(resourceId, UserActionEnum.LIKE));
        detail.setDownloadNum(userActionService.selectCountByResourceIdAndType(resourceId, UserActionEnum.EXCHANGE));
        detail.setCollectNum(userActionService.selectCountByResourceIdAndType(resourceId, UserActionEnum.COLLECT));
        if (!accessToken.equals(NO_TOKEN)) {
            Integer currentUserId = tokenStoreCache.getUser(accessToken).getPkId();
            detail.setIsLike(userActionService.resourceIsAction(currentUserId, resourceId, UserActionEnum.LIKE));
            detail.setIsCollect(userActionService.resourceIsAction(currentUserId, resourceId, UserActionEnum.COLLECT));
            detail.setIsDownload(userActionService.resourceIsAction(currentUserId, resourceId, UserActionEnum.EXCHANGE));
        }
        return detail;
    }

    @Override
    public void publish(ResourcePublishDTO dto) {
        log.info("ResourceServiceImpl.publish dto:{}", dto);
        Integer userId = RequestContext.getUserId();
        Resource resource = ResourceConvert.INSTANCE.convert(dto);
        resource.setAuthor(userId);
        resource.setStatus(ResourceStatusEnum.UNAUDITED.getCode());
        resource.setLikeNum(0);
        log.info("ResourceServiceImpl.publish resource:{}", resource);
        save(resource);
        //记录⽤户投稿⾏为
        userActionService.insertUserAction(userId, resource.getPkId(), UserActionEnum.PUBLISH);
    }
}
