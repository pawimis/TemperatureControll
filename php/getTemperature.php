<?php
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once('vars.php');
    $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
    $query = "SELECT * FROM TemperatureDatabase";
    $results = mysqli_query($dbc,$query);
    if($results === FALSE){
            die(mysql_error());
    }else{
            if(mysqli_num_rows($results) >0){
                    while($rowData = mysqli_fetch_assoc($results)){
                            $row_all[]=array_map("utf8_encode", $rowData);
                    }
            header('Content-type: application/json');
            echo json_encode($row_all);
            }
    }
}

	
?>
