from PersonalIPQuery import app
from flask import request, render_template_string


flag = "/flag"


def check_context(context):

    blacklist = ["_", "encode", "decode", "+", "\"", "'"]

    for item in blacklist:
        if item in context:
            return False

    return True


@app.route('/')
def hello_world():

    if request.headers.getlist("X-Forwarded-For"):
        ip = request.headers.getlist("X-Forwarded-For")[0]

    else:
        ip = request.remote_addr

    context = \
        """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Personal IP query system</title>
        </head>
        <body>
            <h1>
                Your IP: {}
            </h1>
        </body>
        </html>
        """.format(ip)

    if check_context(ip):
        return render_template_string(context)

    else:
        return "hacker!!!Get out!!!"
