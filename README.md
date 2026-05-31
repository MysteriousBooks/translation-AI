# AI Translation Service

基于大模型API的智能翻译后端服务，采用字符数计费模型，支持多语言互译。

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 开发语言 |
| Spring Boot | 2.7.18 | 基础框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 数据库 |
| Alibaba Druid | 1.2.21 | 连接池 |
| Redis | - | 缓存/限流/单端登录/验证码 |
| JWT | 0.11.5 | 认证 |
| Knife4j | 3.0.3 | API文档 |
| Hutool | 5.8.25 | 工具库 |
| Alipay SDK | 4.39.79 | 支付宝支付 |
| Layui | 2.9.8 | 管理后台UI |

### 微信小程序端

| 技术 | 说明 |
|------|------|
| 原生微信小程序 | WXML/WXSS/JS，无第三方框架依赖 |
| 微信登录 | wx.login 获取code，对接后端 /api/app/auth/login/wechat |
| 微信支付 | 小程序支付（requestPayment），需后端适配小程序支付参数 |
| 基础库 | > = 2.20.0 |

## 功能模块

### APP端

| 模块 | 功能 |
|------|------|
| 认证 | 邮箱注册/登录、微信登录、支付宝登录、忘记密码、验证码 |
| 用户 | 个人信息查看/修改、修改密码 |
| 翻译 | 提交翻译、翻译历史、翻译详情 |
| 钱包 | 余额查询、充值（支付宝/微信）、流水记录 |
| 退款 | 申请退款 |
| 其他 | 系统公告、意见反馈 |

### 微信小程序端

| 模块 | 功能 |
|------|------|
| 认证 | 微信授权登录、Token自动管理（401重新登录） |
| 首页 | 多语言翻译（8种语言）、一键复制结果 |
| 历史 | 翻译记录列表、下拉刷新、上拉加载更多 |
| 钱包 | 余额展示、充值（微信支付）、交易流水 |
| 个人 | 编辑资料、修改密码、意见反馈、公告通知 |

### 后台管理

| 模块 | 功能 |
|------|------|
| 认证 | 管理员登录 |
| 看板 | 今日/总览数据统计 |
| 用户管理 | 用户列表、启用/停用 |
| 订单管理 | 充值订单列表、订单详情 |
| 退款管理 | 退款列表、同意/拒绝退款 |
| 消耗流水 | 流水查询 |
| Token统计 | LLM调用统计、按日/周/月 |
| 系统配置 | 翻译定价、支持语种、LLM参数 |
| 公告管理 | 发布/编辑/删除公告 |
| 反馈管理 | 查看/回复用户反馈 |

### 核心特性

- **字符数计费** — 按翻译字符数计费，最低消费可配置
- **单端登录** — 同一账号新登录后旧Token自动失效
- **翻译限流** — 基于Redis的用户级请求频率限制
- **系统配置缓存** — Redis缓存配置项，减少数据库查询
- **管理后台** — Layui + jQuery实现的管理端WEB界面

## 快速开始

### 环境要求

- JDK 1.8+
- MySQL 8.0+
- Redis

### 配置

1. 创建数据库并执行初始化脚本：

```sql
source
src/main/resources/db/V1__init.sql
```

2. 修改 `src/main/resources/application-dev.yml` 中的数据库和Redis连接信息

3. 修改 `src/main/resources/application.yml` 中的以下配置：

```yaml
# LLM翻译配置
translation:
  llm:
    api-url: https://api.openai.com/v1/chat/completions
    api-key: your-api-key
    model: gpt-3.5-turbo

# 邮件配置（验证码）
mail:
  host: smtp.example.com
  username: your-email
  password: your-password

# 支付配置
pay:
  alipay:
    app-id: your-app-id
    private-key: your-private-key
    public-key: alipay-public-key
  wechat:
    app-id: your-app-id
    mch-id: your-mch-id
    api-key: your-api-key
```

### 构建运行

```bash
mvn clean package -DskipTests
java -jar target/translation-1.0.0.jar
```

### 访问地址

| 服务 | 地址 |
|------|------|
| API文档 | http://localhost:8080/doc.html |
| 管理后台 | http://localhost:8080/admin/login.html |
| 微信小程序 | 使用微信开发者工具打开 `miniprogram/` 目录 |

### 默认管理员

| 用户名 | 密码 |
|--------|------|
| admin | admin123 |

> 生产环境请务必修改默认密码

### 微信小程序配置

1. 使用微信开发者工具打开 `miniprogram/` 目录

2. 修改 `miniprogram/utils/constants.js` 中的 `BASE_URL` 为后端API地址

3. 修改 `miniprogram/project.config.json` 中的 `appid` 为你的小程序 AppID

4. 添加 TabBar 图标文件到 `miniprogram/images/` 目录（共8个PNG：4个默认态 + 4个选中态）

5. 在微信公众平台配置后端API的服务器域名

## 数据库设计

共10张表：

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| admin | 管理员表 |
| wallet_record | 钱包流水表 |
| translate_record | 翻译记录表 |
| order | 充值订单表 |
| refund_record | 退款记录表 |
| feedback | 意见反馈表 |
| announcement | 公告表 |
| sys_config | 系统配置表 |
| token_statistics | Token统计表 |

## 项目结构

```
miniprogram/                          # 微信小程序前端
├── app.js                            # 小程序入口（初始化、登录检查）
├── app.json                          # 全局配置（页面路由、tabBar、窗口样式）
├── app.wxss                          # 全局样式
├── project.config.json               # 项目配置
├── sitemap.json                      # 站点地图
├── utils/                            # 工具模块
│   ├── request.js                    # 统一请求封装（JWT携带、错误处理、Token刷新）
│   ├── auth.js                       # 认证模块（微信登录、Token存取）
│   ├── util.js                       # 通用工具（日期格式化、金额格式化、文本截断）
│   └── constants.js                  # 常量（API路径、状态码映射、语言选项）
├── components/                       # 公共组件
│   ├── empty-state/                  # 空状态提示
│   ├── loading-more/                 # 上拉加载更多
│   ├── translate-card/               # 翻译记录卡片
│   └── order-card/                   # 充值订单卡片
├── pages/
│   ├── index/                        # 首页（翻译）
│   ├── history/                      # 翻译历史
│   ├── history-detail/               # 翻译详情
│   ├── wallet/                       # 钱包（余额+充值+流水）
│   ├── recharge/                    # 充值（选择金额+微信支付）
│   ├── profile/                      # 个人中心
│   ├── profile-edit/                 # 编辑个人信息
│   ├── password/                     # 修改密码
│   ├── feedback/                     # 意见反馈
│   ├── notice-list/                  # 公告列表
│   └── notice-detail/               # 公告详情
└── images/                           # 图标和静态图片

src/main/java/com/translation/
├── TranslationApplication.java
├── common/
│   ├── config/          # 配置类（Redis、CORS、MyBatis-Plus、Knife4j、WebMvc）
│   ├── constant/        # 常量
│   ├── enums/           # 枚举（7个）
│   ├── exception/       # 异常处理
│   ├── result/          # 统一响应
│   └── utils/           # 工具类（JwtUtil, RedisUtil, UserContext）
├── interceptor/          # 拦截器（APP认证+单端登录校验、Admin认证）
├── entity/              # 数据库实体（10个）
├── mapper/              # MyBatis Mapper（10个）
├── service/             # 服务接口
│   ├── impl/            # 服务实现
│   └── pay/             # 支付服务（支付宝/微信）
├── controller/
│   ├── app/             # APP端接口（7个Controller）
│   └── admin/           # 后台接口（9个Controller）
├── dto/                 # 请求参数
│   ├── app/             # APP端DTO（11个）
│   └── admin/           # 后台DTO（4个）
└── vo/                  # 响应视图
    ├── app/             # APP端VO（6个）
    └── admin/           # 后台VO（6个）

src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.yml          # 开发环境配置
├── application-prod.yml         # 生产环境配置
├── db/
│   └── V1__init.sql             # 数据库初始化脚本
└── static/admin/                # 管理后台WEB
    ├── login.html               # 登录页
    ├── index.html               # 主框架（侧边栏导航）
    ├── js/
    │   └── common.js            # 公共JS（API封装、认证检查、状态映射）
    └── pages/
        ├── dashboard.html       # 数据看板
        ├── user.html            # 用户管理
        ├── order.html           # 订单管理
        ├── refund.html          # 退款管理
        ├── consume.html         # 消耗流水
        ├── token.html           # Token统计
        ├── config.html          # 系统配置
        ├── notice.html          # 公告管理
        └── feedback.html        # 反馈管理
```

## API接口

### APP端（`/api/app/`）

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | /auth/register | POST | 邮箱注册 |
| 认证 | /auth/login | POST | 邮箱登录 |
| 认证 | /auth/wechat | POST | 微信登录 |
| 认证 | /auth/alipay | POST | 支付宝登录 |
| 认证 | /auth/forgot-password | POST | 忘记密码 |
| 认证 | /auth/send-code | POST | 发送验证码 |
| 用户 | /user/info | GET | 获取用户信息 |
| 用户 | /user/update | PUT | 修改用户信息 |
| 用户 | /user/change-password | PUT | 修改密码 |
| 翻译 | /translate | POST | 提交翻译 |
| 翻译 | /translate/history | GET | 翻译历史 |
| 翻译 | /translate/{id} | GET | 翻译详情 |
| 钱包 | /wallet/balance | GET | 余额查询 |
| 钱包 | /wallet/records | GET | 流水记录 |
| 钱包 | /wallet/recharge/alipay | POST | 支付宝充值 |
| 钱包 | /wallet/recharge/wechat | POST | 微信充值 |
| 退款 | /refund/apply | POST | 申请退款 |
| 退款 | /refund/list | GET | 退款列表 |
| 反馈 | /feedback/submit | POST | 提交反馈 |
| 反馈 | /feedback/list | GET | 反馈列表 |
| 公告 | /notice/list | GET | 公告列表 |

### 后台管理（`/api/admin/`）

| 模块 | 接口 | 方法 | 说明 |
|------|------|------|------|
| 认证 | /auth/login | POST | 管理员登录 |
| 看板 | /dashboard/overview | GET | 数据总览 |
| 用户 | /user/list | GET | 用户列表 |
| 用户 | /user/{id} | GET | 用户详情 |
| 用户 | /user/{id}/status | PUT | 启用/停用用户 |
| 订单 | /order/list | GET | 订单列表 |
| 订单 | /order/{id} | GET | 订单详情 |
| 退款 | /refund/list | GET | 退款列表 |
| 退款 | /refund/{id}/approve | PUT | 同意退款 |
| 退款 | /refund/{id}/reject | PUT | 拒绝退款 |
| 消耗 | /consume/list | GET | 消耗流水 |
| Token | /token/statistics | GET | Token汇总统计 |
| Token | /token/detail | GET | Token明细列表 |
| 配置 | /config/list | GET | 配置列表 |
| 配置 | /config/{key} | PUT | 更新配置 |
| 公告 | /notice/list | GET | 公告列表 |
| 公告 | /notice | POST | 新建公告 |
| 公告 | /notice/{id} | PUT | 编辑公告 |
| 公告 | /notice/{id} | DELETE | 删除公告 |
| 反馈 | /feedback/list | GET | 反馈列表 |
| 反馈 | /feedback/{id}/reply | PUT | 回复反馈 |

## 开源许可

本项目基于 [MIT License](https://opensource.org/licenses/MIT) 开源。

```
MIT License

Copyright (c) 2024

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```