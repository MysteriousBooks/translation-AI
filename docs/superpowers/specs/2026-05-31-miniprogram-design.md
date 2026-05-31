# 微信小程序端设计方案

## 概述

为现有AI翻译服务后端（Spring Boot）添加微信小程序前端，接入APP端已有API接口。采用原生微信小程序开发，模块化封装架构，简约清爽UI风格。

## 技术选型

| 项目 | 选择 |
|---|---|
| 框架 | 原生微信小程序（WXML/WXSS/JS） |
| 认证 | 微信授权登录（复用 /api/app/auth/login/wechat） |
| UI风格 | 简约清爽（白色背景、蓝色主色调、圆角卡片） |

## 项目结构

```
miniprogram/                          # 小程序项目根目录
├── app.js                            # 小程序入口（初始化、登录检查）
├── app.json                          # 全局配置（页面路由、tabBar、窗口样式）
├── app.wxss                          # 全局样式
├── project.config.json               # 项目配置
├── sitemap.json                      # 站点地图
├── utils/                            # 工具模块
│   ├── request.js                    # 统一请求封装（JWT携带、错误处理、Token刷新）
│   ├── auth.js                       # 认证模块（微信登录、Token存取、登录态检查）
│   ├── util.js                       # 通用工具（日期格式化、文本截断等）
│   └── constants.js                  # 常量（API路径、状态码映射等）
├── components/                       # 公共组件
│   ├── empty-state/                  # 空状态组件
│   ├── loading-more/                 # 加载更多组件
│   ├── translate-card/               # 翻译记录卡片组件
│   └── order-card/                   # 充值订单卡片组件
├── pages/
│   ├── index/                        # 首页（翻译）
│   ├── history/                      # 翻译历史
│   ├── history-detail/               # 翻译详情
│   ├── wallet/                       # 钱包（余额+充值+流水）
│   ├── recharge/                    # 充值（选择金额+支付）
│   ├── profile/                      # 个人中心
│   ├── profile-edit/                 # 编辑个人信息
│   ├── password/                     # 修改密码
│   ├── feedback/                     # 意见反馈
│   ├── notice-list/                  # 公告列表
│   └── notice-detail/               # 公告详情
└── images/                           # 图标和静态图片
```

## TabBar 配置

4个Tab：首页（翻译）、历史、钱包、我的

| Tab | 页面路径 |
|---|---|
| 首页 | pages/index/index |
| 历史 | pages/history/history |
| 钱包 | pages/wallet/wallet |
| 我的 | pages/profile/profile |

## 页面功能设计

### TabBar 页面

**首页（翻译）** - `pages/index`
- 语言对选择（中文/英语/日语/韩语/法语/德语/西班牙语/俄语）
- 多行文本输入（最大5000字符）
- 翻译按钮，调用 POST /api/app/translate
- 翻译结果展示区域，支持一键复制
- 输入字符计数

**历史** - `pages/history`
- 翻译记录列表，下拉刷新、上拉加载更多
- 每条记录显示：原文摘要、译文摘要、语言对、时间
- 点击进入翻译详情

**钱包** - `pages/wallet`
- 顶部余额卡片
- 充值按钮，跳转充值页
- 钱包流水列表（收入/支出）

**我的** - `pages/profile`
- 头像、昵称、余额
- 功能入口：编辑资料、修改密码、意见反馈、公告

### 非 Tab 页面

| 页面 | 功能 | 对应API |
|---|---|---|
| history-detail | 翻译详情（原文/译文/字数/费用/状态） | GET /api/app/translate/{id} |
| recharge | 选择充值金额、确认支付 | POST /api/app/wallet/recharge |
| profile-edit | 修改昵称/头像 | PUT /api/app/user/info |
| password | 修改密码 | PUT /api/app/user/password |
| feedback | 提交反馈内容 | POST /api/app/feedback |
| notice-list | 公告列表 | GET /api/app/notice/list |
| notice-detail | 公告详情 | - |

## 认证流程

### 微信登录

```
小程序启动 → 检查本地Token
  ├── Token有效 → 直接进入首页
  └── Token无效/过期 → 调用wx.login获取code
       → POST /api/app/auth/login/wechat {code}
       → 后端返回 {token, userInfo}
       → 存储Token到wx.setStorageSync
       → 进入首页
```

### Token管理

- 存储位置：`wx.getStorageSync('token')`
- 请求自动携带：`Authorization: Bearer {token}`
- Token过期（401响应）：自动清除本地Token，重新触发微信登录
- 单端登录：后端已有单端限制，小程序登录会踢掉APP端同一用户

## API封装层设计

### request.js

- 自动携带 `Authorization: Bearer {token}`
- 统一错误处理：401自动重新登录，业务错误弹提示
- 统一loading控制（可选）
- 基础URL配置（开发/生产环境切换）

### API接口映射

| 模块 | 方法 | 接口路径 |
|---|---|---|
| auth | login | POST /api/app/auth/login/wechat |
| translate | submit | POST /api/app/translate |
| translate | history | GET /api/app/translate/history |
| translate | detail | GET /api/app/translate/{id} |
| user | getInfo | GET /api/app/user/info |
| user | updateInfo | PUT /api/app/user/info |
| user | updatePassword | PUT /api/app/user/password |
| wallet | balance | GET /api/app/wallet/balance |
| wallet | records | GET /api/app/wallet/records |
| wallet | recharge | POST /api/app/wallet/recharge |
| wallet | rechargeStatus | GET /api/app/wallet/recharge/status/{orderNo} |
| feedback | submit | POST /api/app/feedback |
| notice | list | GET /api/app/notice/list |
| refund | apply | POST /api/app/refund/apply |

## 公共组件设计

| 组件 | 功能 | 使用场景 |
|---|---|---|
| empty-state | 空状态提示（图标+文字+按钮） | 历史为空、流水为空等 |
| loading-more | 上拉加载更多（加载中/没有更多） | 历史列表、流水列表 |
| translate-card | 翻译记录卡片（原文摘要/译文摘要/时间/语言对） | 历史列表 |
| order-card | 充值订单卡片（订单号/金额/状态/时间） | 流水列表 |

## UI设计规范

### 色彩体系

| 用途 | 色值 | 说明 |
|---|---|---|
| 主色 | #4A90D9 | 蓝色，专业翻译感 |
| 成功 | #52C41A | 绿色 |
| 警告 | #FAAD14 | 黄色 |
| 错误 | #F5222D | 红色 |
| 背景 | #F5F5F5 | 浅灰背景 |
| 卡片 | #FFFFFF | 白色 |

### 字体规范

| 用途 | 大小 | 字重 |
|---|---|---|
| 标题 | 32rpx | 500 |
| 正文 | 28rpx | 400 |
| 辅助文字 | 24rpx | 400 |

### 间距规范

统一使用16rpx的倍数：16/24/32/48

### 卡片样式

- 白色背景
- 圆角：16rpx
- 阴影：0 2rpx 12rpx rgba(0,0,0,0.08)
- 内边距：32rpx

## 首页（翻译）布局

```
┌─────────────────────┐
│   语言选择栏          │
│   [中文] ⇄ [英语]    │
├─────────────────────┤
│                     │
│   文本输入区域        │
│   （多行文本框）      │
│                     │
├─────────────────────┤
│  已输入 0/5000 字    │
├─────────────────────┤
│   [  翻  译  ]      │
├─────────────────────┤
│                     │
│   翻译结果区域        │
│   （可复制）          │
│                     │
├─────────────────────┤
│   [复制] [查看详情]   │
└─────────────────────┘
```

## 后端注意事项

1. **微信登录接口**：需确认 `/api/app/auth/login/wechat` 的参数格式，小程序端传 `code`（通过 `wx.login` 获取），后端用code换取openid
2. **CORS**：小程序不需要CORS，但需确保后端允许小程序的请求域名
3. **微信支付**：充值功能需后端配合微信支付API（小程序支付与APP支付参数不同，需后端适配）
4. **单端登录**：小程序登录会踢掉APP端，如有需要可在后端区分端
5. **小程序服务器域名**：需在微信公众平台配置后端API域名

## 依赖

- 微信小程序基础库 >= 2.20.0
- 无第三方npm依赖（纯原生开发）