FROM python:3
ENV PYTHONUNBUFFERED 1

WORKDIR /code
ADD requirements.txt /code/
RUN pip install -r requirements.txt
ADD . /code/

WORKDIR /code/
VOLUME /code
EXPOSE 8000
CMD ["python","app.py"]