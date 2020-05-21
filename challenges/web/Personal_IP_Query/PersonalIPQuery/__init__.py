from flask import Flask
import os


def create_flag_file():
    flag = ''
    try:
        flag = os.environ["flag"]
        del os.environ['flag']
    except KeyError:
        pass

    try:
        flag = os.environ["FLAG"]
        del os.environ['FLAG']

    except:
        flag = 'minilCTF{123456}'

    flag = flag
    with open("/flag", "w") as f:
        f.write(flag)


create_flag_file()

del os

app = Flask(__name__)

import PersonalIPQuery.views

if __name__ == '__main__':
    app.run()
