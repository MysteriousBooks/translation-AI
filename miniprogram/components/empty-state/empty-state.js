Component({
    properties: {
        icon: {
            type: String,
            value: ''
        },
        text: {
            type: String,
            value: '暂无数据'
        },
        buttonText: {
            type: String,
            value: ''
        }
    },
    methods: {
        onButtonClick() {
            this.triggerEvent('buttonClick')
        }
    }
})
