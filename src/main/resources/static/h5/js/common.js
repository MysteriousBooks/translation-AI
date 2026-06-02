/* H5用户端公共JS */
var API_BASE = '/api/app';
var TOKEN_KEY = 'h5_token';
var USER_ID_KEY = 'h5_user_id';
var USER_NICKNAME_KEY = 'h5_nickname';

/* API请求封装 */
var H5 = {
    /* GET请求 */
    get: function (url, data, callback) {
        $.ajax({
            url: API_BASE + url,
            type: 'GET',
            data: data,
            headers: {'Authorization': 'Bearer ' + localStorage.getItem(TOKEN_KEY)},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    H5._handleUnauthorized();
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    H5._handleUnauthorized();
                } else {
                    H5.toast('网络错误，请稍后重试');
                    callback && callback(null);
                }
            }
        });
    },
    /* POST请求 */
    post: function (url, data, callback) {
        $.ajax({
            url: API_BASE + url,
            type: 'POST',
            contentType: 'application/json',
            data: typeof data === 'string' ? data : JSON.stringify(data),
            headers: {'Authorization': 'Bearer ' + localStorage.getItem(TOKEN_KEY)},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    H5._handleUnauthorized();
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    H5._handleUnauthorized();
                } else {
                    H5.toast('网络错误，请稍后重试');
                    callback && callback(null);
                }
            }
        });
    },
    /* PUT请求 */
    put: function (url, data, callback) {
        $.ajax({
            url: API_BASE + url,
            type: 'PUT',
            contentType: 'application/json',
            data: typeof data === 'string' ? data : JSON.stringify(data),
            headers: {'Authorization': 'Bearer ' + localStorage.getItem(TOKEN_KEY)},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    H5._handleUnauthorized();
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    H5._handleUnauthorized();
                } else {
                    H5.toast('网络错误，请稍后重试');
                    callback && callback(null);
                }
            }
        });
    },
    /* 统一处理 401/Token 失效：清空本地凭据并跳转登录页（避免重复回调） */
    _unauthorizedHandled: false,
    _handleUnauthorized: function () {
        if (H5._unauthorizedHandled) return;
        H5._unauthorizedHandled = true;
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_ID_KEY);
        localStorage.removeItem(USER_NICKNAME_KEY);
        window.location.href = '/h5/login.html';
    },
    /* 检查登录状态 */
    checkAuth: function () {
        if (!localStorage.getItem(TOKEN_KEY)) {
            window.location.href = '/h5/login.html';
        }
    },
    /* 退出登录 */
    logout: function () {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_ID_KEY);
        localStorage.removeItem(USER_NICKNAME_KEY);
        window.location.href = '/h5/login.html';
    },
    /* Toast提示 */
    toast: function (msg, duration) {
        duration = duration || 2000;
        var el = document.createElement('div');
        el.className = 'h5-toast';
        el.textContent = msg;
        document.body.appendChild(el);
        setTimeout(function () {
            if (el.parentNode) el.parentNode.removeChild(el);
        }, duration);
    },
    /* 格式化时间 */
    formatTime: function (str) {
        if (str === null || str === undefined || str === '') return '';
        return String(str).replace('T', ' ').substring(0, 19);
    },
    /* 格式化金额（使用 toFixed 避免 parseFloat 精度丢失） */
    formatMoney: function (val) {
        if (val === null || val === undefined || val === '') return '0.00';
        var n = Number(val);
        if (isNaN(n)) return '0.00';
        return n.toFixed(2);
    }
};

/* HTML转义（防XSS） */
function escapeHtml(text) {
    if (!text) return '';
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
}

/* 状态映射 */
var WalletRecordTypeMap = {1: '充值', 2: '消耗', 3: '退款'};
var TranslateStatusMap = {0: '翻译中', 1: '成功', 2: '失败'};
var OrderStatusMap = {0: '待支付', 1: '已支付', 2: '已取消', 3: '已退款'};
var PayTypeMap = {1: '支付宝', 2: '微信'};
var NoticeTypeMap = {1: '通知', 2: '公告'};
var NoticeStatusMap = {0: '草稿', 1: '已发布'};

/* 语言映射 */
var LangMap = {
    'zh-CN': '中文',
    'en': '英语',
    'ja': '日语',
    'ko': '韩语',
    'fr': '法语',
    'de': '德语',
    'es': '西班牙语',
    'ru': '俄语'
};

/* 生成底部TabBar HTML字符串（移动端） */
function renderTabbar(activeIndex) {
    var items = [
        {name: '翻译', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 8l6 6"/><path d="M4 14l6-6"/><path d="M2 5h6"/><path d="M2 19h6"/><path d="M14 4l4 8"/><path d="M22 4l-4 8"/><path d="M14 20h2c2.2 0 4-1.8 4-4V4"/></svg>', href: '/h5/index.html'},
        {name: '历史', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>', href: '/h5/pages/history.html'},
        {name: '钱包', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M2 10h20"/></svg>', href: '/h5/pages/wallet.html'},
        {name: '我的', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>', href: '/h5/pages/profile.html'}
    ];
    var html = '<div class="h5-tabbar">';
    for (var i = 0; i < items.length; i++) {
        html += '<a class="h5-tabbar-item' + (i === activeIndex ? ' active' : '') + '" href="' + items[i].href + '">';
        html += items[i].icon;
        html += '<span>' + items[i].name + '</span></a>';
    }
    html += '</div>';
    return html;
}

/* 生成PC侧边栏 */
function renderSidebar(activeIndex) {
    var items = [
        {name: '翻译', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 8l6 6"/><path d="M4 14l6-6"/><path d="M2 5h6"/><path d="M2 19h6"/><path d="M14 4l4 8"/><path d="M22 4l-4 8"/><path d="M14 20h2c2.2 0 4-1.8 4-4V4"/></svg>', href: '/h5/index.html'},
        {name: '历史', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>', href: '/h5/pages/history.html'},
        {name: '钱包', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M2 10h20"/></svg>', href: '/h5/pages/wallet.html'},
        {name: '我的', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>', href: '/h5/pages/profile.html'}
    ];
    var html = '<div class="h5-pc-sidebar">';
    for (var i = 0; i < items.length; i++) {
        html += '<a class="h5-pc-sidebar-item' + (i === activeIndex ? ' active' : '') + '" href="' + items[i].href + '">';
        html += items[i].icon;
        html += '<span>' + items[i].name + '</span></a>';
    }
    html += '</div>';
    return html;
}