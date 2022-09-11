[![](https://img.shields.io/badge/Made_with-SpringBoot-white?style=for-the-badge&logo=Springboot&logoColor=green)]( https://spring.io/ "Spring Boot")[![](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/ "Maven")[![](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/ "Postgrsql")[![](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/ "Docker")[![](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white)](https://www.jenkins.io/ "Jenkins")[![](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](https://www.postman.com/ "Postman")
<span><h1 align="center">Ecommerce Backend Spring Boot Application</h1></span>

<img style="width:80px;" src="https://e4developer.com/wp-content/uploads/2018/01/spring-boot.png" /> Java Springboot backend for production grade ecommerce enterprise. 
The project contains modules for **Authentication, Inventory Management, Shopping Cart, Wish List, Ordering, Delivery Status and Payment Gateway using RazorPay**

<h2>Authentication</h2>
Cookie based Authentication. Two-step email based Registration. Only two roles define <em>ADMIN_ROLE</em> and <em>USER_ROLE</em>. Admin has certain priviliges which a generic user doesn't. The admin privilidges are to manage inventory, look at user details, user authority, shopping cart, wishlist, orders, payment status, delivery status of all users 

<h2>Inventory Management</h2>
Products can be added and deleted one at a time and in a list. The product can be updated one at a time. Addition of products with Excel Sheets

<h2>Shopping Cart & WishList</h2>
Crud based operation for Shopping Cart and Wish List. Same product with different product attributes can be to Shopping Cart but not the Wish List

<h2>Ordering and Delivery Status</h2>
A product attempted to buy is added to the list of Orders and the payment status determines a successful order. Delivery Status is updated to Received, Packaging , Dispatched or Delivered.

<h2>Payment Gateway</h2>
Razorpay gateway is used for making payments and keeping track of payment status

<h2>Postgresql Database Schema <img style="width:30px;" src="https://icons-for-free.com/iconfiles/png/512/postgresql+plain-1324760555607314126.png" /></h2>
<img src ="https://user-images.githubusercontent.com/46090098/189498678-6d610fd4-cee7-4172-a3e5-d8730c9edf7f.png" alt="database schema"/>

<h2>Docker <img style="width:30px;" src="https://www.docker.com/wp-content/uploads/2022/03/vertical-logo-monochromatic.png" /></h2>
The <em>Dockerfile</em> builds a containerized application for the build version of maven project. 
<em>docker-compose.yml</em> file defines the building a container from  database image and also building springboot backend container using the <em>Dockerfile</em> defined in the folder

<h2>Jenkins <img style="width:30px;" src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/Jenkins_logo.svg/1200px-Jenkins_logo.svg.png" /></h2>
The <em>Jenkinsfile</em> builds the entire project on the local machine with <b>Maven</b> and builds multi-container image for docker application using <b>Compose</b>. Once the images are build, they are pushed to the jfrog artifactory with latest tags. On the server machine, it pulls the latest tagged images and runs the the multi-container docker application on the server.
<br/>
<br/>
<i>Refer another similar ecommerce flutter and firebase based application <a href="https://github.com/Rahil-Parikh/Ecommerce-market">here</a></i><br/>
<b>Note : This project is tailored to a <a href="https://tinyurl.com/369572us">jewelry ecommerce website</a> with products having attributes similar to jewelry products</b>
