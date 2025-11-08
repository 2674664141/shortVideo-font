package com.xingzhi.shortvideosharingplatform.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPrivacy {

  private Long id;//主键ID
  private Long userId;//用户ID
  private Integer accountPublic;//账号是否公开：0-私密 1-公开
  private Integer allowFollow;//允许关注：0-需要验证 1-直接关注
  private Integer allowComment;//允许评论：0-不允许 1-所有人 2-仅关注的人
  private Integer allowMessage;//允许私信：0-不允许 1-所有人 2-仅关注的人 3-仅互相关注
  private Integer allowRecommendation;//允许个性化推荐：0-不允许 1-允许
  private Integer showOnlineStatus;//显示在线状态：0-不显示 1-显示
  private Integer showViewHistory;//显示浏览记录：0-不显示 1-显示
  private Integer allowSearch;//允许被搜索：0-不允许 1-允许
  private Integer showFollowerList;//显示粉丝列表：0-不显示 1-显示
  private Integer showFollowingList;//显示关注列表：0-不显示 1-显示
  private Integer allowDownload;//允许下载视频：0-不允许 1-允许
  private Integer contentFilter;//内容过滤：0-关闭 1-基础过滤 2-严格过滤
  private LocalDateTime createdTime;//创建时间
  private LocalDateTime updatedTime;//更新时间
}
