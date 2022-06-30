<?php

// set header to json
header('Content-Type: application/json');

include 'config.php';

// check if method is POST
if($_SERVER['REQUEST_METHOD'] != 'POST') {
    echo json_encode(array('error' => 'Invalid request method'));
    exit();
}   

// check if the request has params
if(!isset($_FILES['image'])){
    echo json_encode(array('error' => 'File not exist!'));
    exit();
}

// get the image
$fileName = time() . rand() . '.' . pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
$filePath = $_FILES['image']['tmp_name'];
$fileSize = $_FILES['image']['size'];
$fileExtn = strtolower(pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION));

// check if image is not empty
if(empty($filePath)) {
    echo json_encode(array('error' => 'File is empty!'));
    exit();
}

$upload_path = 'api/uploads/images/';

// check if extension is valid
$allowed_extensions = array('jpg', 'jpeg', 'png', 'gif');

if(in_array($fileExtn, $allowed_extensions) === false) {
    echo json_encode(array('error' => 'Invalid file extension!'));
    exit();
}

// check file size
if($fileSize > 5000000) {
    echo json_encode(array('error' => 'Maximum file size is 5MB!'));
    exit();
}

move_uploaded_file($filePath, $upload_path.$fileName);

// upload to database
$query = "INSERT INTO images (name) VALUES ('$fileName')";
$result = mysqli_query($db, $query);

// return response if query is successful
if($result) {
    echo json_encode(array('success' => 'Image uploaded successfully!'));
} else {
    echo json_encode(array('error' => 'Query failed!'));
}