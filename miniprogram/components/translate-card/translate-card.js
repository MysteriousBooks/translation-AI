const {formatTime, truncateText, getLangName} = require('../../utils/util')

Component({
    properties: {
        item: {
            type: Object,
            value: {}
        }
    },
    data: {
        sourceText: '',
        translatedText: '',
        langPair: '',
        createTime: ''
    },
    observers: {
        'item': function (item) {
            if (item) {
                this.setData({
                    sourceText: truncateText(item.sourceText, 40),
                    translatedText: truncateText(item.translatedText, 40),
                    langPair: getLangName(item.sourceLang) + ' → ' + getLangName(item.targetLang),
                    createTime: formatTime(item.createTime)
                })
            }
        }
    },
    methods: {
        onCardClick() {
            this.triggerEvent('click', {id: this.data.item.id})
        }
    }
})
