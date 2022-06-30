<?php

// your server configuration
$db_host = 'localhost';
$db_user = 'root';
$db_pass = '';
$db_name = 'db_uploadretrofit';

// connect to the database
$db = mysqli_connect($db_host, $db_user, $db_pass, $db_name);

//check if connection is successful
if(mysqli_connect_errno()) {
    echo 'Database connection failed with following errors: '.mysqli_connect_error();
    die();
}