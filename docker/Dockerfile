FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt update && apt install -y \
    openjdk-21-jdk \
    nginx \
    openssh-server \
    git \
    wget \
    curl \
    php-cli \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir /var/run/sshd

RUN echo "root:123" | chpasswd

RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

RUN sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

WORKDIR /app

COPY target/vicheth_sokhsedtha-0.0.1-SNAPSHOT.jar app.jar

COPY nginx.conf /etc/nginx/nginx.conf

COPY start.sh /start.sh

RUN chmod +x /start.sh

EXPOSE 8060

EXPOSE 22

CMD ["/start.sh"]