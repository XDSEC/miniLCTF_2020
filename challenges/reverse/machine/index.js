var machine = {
    0x0016: {},
    0x1245: function base64encode(str) {
        try {
            return Buffer.from(str, 'utf8').toString('base64');
        } catch (e) {
            return btoa(str)
        }
    },
    0x1428: function(arr, index, value) {
        arr[index] = value
    },
    0x8752: function(k, v) {
        this[0x0016][k] = v
    },
    0: function(x) {
        var a = this[0x1245](x)
        a = a.split('').reverse().join('')
        var b = []
        for(var i=0;i<a.length;i++) {
            b.push(String.fromCharCode(a.charCodeAt(i) ^ i))
        }
        return b.join('')
    },
    'ccc': function (f, p, p2, p3) {
        return f(p, p2, p3)
    },
    'qqq': function(x) {
        console.log(x)
    },
    'cccddd': function(f) {
        this[0x1428](this[0x0016], 's', f)
        this[0x8752](
            't', setTimeout(function () {
                var aa = 'flag_here'
                machine[0x0016]['s'](aa)
            }, 10)
        )
    },
    asdf: function (flag) {
        machine[0x0016] = [];
        for(var i in flag.split('-')) {
            machine[0x8752](machine[0x0016].length, machine[0](
                flag.split('-')[i]
            ))
        }
    }
}
machine.cccddd(machine.asdf)
