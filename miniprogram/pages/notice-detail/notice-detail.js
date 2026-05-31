Page({
    data: {
        notice: null
    },

    onLoad(options) {
        // 从列表页传过来的公告数据
        const pages = getCurrentPages()
        const prevPage = pages[pages.length - 2]
        if (prevPage) {
            const list = prevPage.data.list || []
            const notice = list.find(item => item.id === Number(options.id))
            if (notice) {
                this.setData({notice})
                wx.setNavigationBarTitle({title: notice.title || '公告详情'})
            }
        }
    }
})
