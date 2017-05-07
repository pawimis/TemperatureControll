<?php
require_once('vars.php');
    $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
           or die('No SQL server connection');
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	$content = trim(file_get_contents("php://input"));
	$json_array = json_decode($content,true);
	echo 'debug';
	if($json_array != null){
		$temperature = $json_array['TEMPERATURE'];
		$controller = $json_array['CONTROLLER'];
		$room = $json_array['ROOM'];
		$query = "INSERT INTO TemperatureDatabase(ROOM,DATE,TIME,TEMPERATURE,CONTROLLER)".
		                    "VALUES('$room',CURDATE(),CURTIME(),'$temperature','$controller')";
			mysqli_query($dbc,$query)
				or die('ERROR in query');
		echo 'succes';
	}	
	mysqli_close($dbc);
	exit;
}

?>
