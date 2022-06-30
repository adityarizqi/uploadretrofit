<?php

// set header to json
header ('Content-Type: application/json');

include 'config.php';

// get image from the database
$query = "SELECT * FROM images";
$result = mysqli_query($db, $query);

// check if query is successful
if(!$result) {
    echo json_encode(array('error' => 'Query failed!'));
    exit();
}

// parsing data into array
$data = array();
while($row = mysqli_fetch_assoc($result)) {
    $data['id'] = $row['id'];
    $data['name'] = $row['name'];
    $data['url'] = 'http://' . $_SERVER['HTTP_HOST'] . '/uploadretrofit/uploads/images/' . $row['name'];
}

// return json as response
echo json_encode($data);