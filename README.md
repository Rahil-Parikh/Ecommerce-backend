[![](https://img.shields.io/badge/Made_with-SpringBoot-white?style=for-the-badge&logo=Springboot&logoColor=green)]( https://spring.io/ "Spring Boot")[![](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/ "Maven")[![](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/ "Postgrsql")[![](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/ "Docker")[![](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white)](https://www.jenkins.io/ "Jenkins")[![](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white)](https://www.postman.com/ "Postman")
<span><h1 align="center">Ecommerce Backend Spring Boot Application</h1></span>

Java Springboot backend for production grade ecommerce enterprise. 
The project contains modules for **Authentication, Inventory Management, Shopping Cart, Wish List, Ordering, Delivery Status and Payment Gateway using RazorPay**

<h2>Authentication</h2>
Cookie based Authentication. Two-step email based Registration. Only two roles define ADMIN_ROLE and USER_ROLE. Admin has certain priviliges which a generic user doesn't. The admin privilidges are to manage inventory, look at user details, user authority, shopping cart, wishlist, orders, payment status, delivery status of all users 

<h2>Inventory Management</h2>
Products can be added and deleted one at a time and in a list. The product can be updated one at a time. Addition of products with Excel Sheets

<h2>Shopping Cart & WishList</h2>
Crud based operation for Shopping Cart and Wish List. Same product with different product attributes can be to Shopping Cart but not the Wish List

<h2>Ordering and Delivery Status</h2>
A product attempted to buy is added to the list of Orders and the payment status determines a successful order. Delivery Status is updated to Received, Packaging , Dispatched or Delivered.

<h2>Payment Gateway</h2>
Razorpay gateway is used for making payments and keeping track of payment status

<h2>Postgresql Database Schema</h2>
<img src ="https://user-images.githubusercontent.com/46090098/189498678-6d610fd4-cee7-4172-a3e5-d8730c9edf7f.png" alt="database schema"/>

<h2>Docker</h2>
The DOCKERFILE builds a containerized application for the build version of maven project. docker-compose.yml file defines the building a container from  database image and also building uspringboot backend container using the DOCKERFILE defined in the folder

<h2>Jenkins</h2>
Builds the entire project from the local machine with **Maven** and builds multi-container image for docker application with **Compose**. After the images are build, they are pushed to the jfrog artifactory with latest tags. Later, from the server machine, it pulls the latest tagged images and runs the the multi-container docker application on the server.


**Note : This project is tailored to a jewelry ecommerce backend with products having attributes similar to jewelry products**


