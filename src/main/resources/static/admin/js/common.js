/* 管理后台公共JS */
var API = {
    get: function (url, data, callback) {
        $.ajax({
            url: url,
            type: 'GET',
            data: data,
            headers: {'Authorization': 'Bearer ' + localStorage.getItem('admin_token')},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                }
            }
        });
    },
    post: function (url, data, callback) {
        $.ajax({
            url: url,
            type: 'POST',
            contentType: 'application/json',
            data: typeof data === 'string' ? data : JSON.stringify(data),
            headers: {'Authorization': 'Bearer ' + localStorage.getItem('admin_token')},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                }
            }
        });
    },
    put: function (url, data, callback) {
        $.ajax({
            url: url,
            type: 'PUT',
            contentType: 'application/json',
            data: typeof data === 'string' ? data : JSON.stringify(data),
            headers: {'Authorization': 'Bearer ' + localStorage.getItem('admin_token')},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                }
            }
        });
    },
    del: function (url, callback) {
        $.ajax({
            url: url,
            type: 'DELETE',
            headers: {'Authorization': 'Bearer ' + localStorage.getItem('admin_token')},
            success: function (res) {
                if (res.code === 1001 || res.code === 1002) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                    return;
                }
                callback && callback(res);
            },
            error: function (xhr) {
                if (xhr.status === 401) {
                    localStorage.removeItem('admin_token');
                    window.location.href = '/admin/login.html';
                }
            }
        });
    }
};

/* 检查登录状态 */
function checkAuth() {
    if (!localStorage.getItem('admin_token')) {
        window.location.href = '/admin/login.html';
    }
}

/* 退出登录 */
function logout() {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_id');
    localStorage.removeItem('admin_nickname');
    window.location.href = '/admin/login.html';
}

/* 状态映射 */
var OrderStatusMap = {0: '待支付', 1: '已支付', 2: '已取消', 3: '已退款'};
var RefundStatusMap = {0: '待审核', 1: '已同意', 2: '已拒绝', 3: '退款中', 4: '已完成'};
var PayTypeMap = {1: '支付宝', 2: '微信'};
var UserStatusMap = {0: '停用', 1: '正常'};
var LoginTypeMap = {1: '邮箱', 2: '微信', 3: '支付宝'};
var WalletRecordTypeMap = {1: '充值', 2: '消耗', 3: '退款'};
var NoticeStatusMap = {0: '草稿', 1: '已发布'};
var FeedbackStatusMap = {0: '未处理', 1: '已回复'};