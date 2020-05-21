# this is an official Python runtime, used as the parent image
FROM python:3.7

# workdir
WORKDIR /usr/src/app

# copy files
COPY . .

# install flask
RUN pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple -r ./requirements.txt

# unblock port 80 for the Flask app to run on
EXPOSE 80

# create flag file and start server
CMD ["gunicorn", "PersonalIPQuery:app", "-c", "./gunicorn.conf.py"]