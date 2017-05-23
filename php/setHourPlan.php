<?php
require_once('vars.php');
    $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME)
           or die('No SQL server connection');
if($_SERVER['REQUEST_METHOD'] == 'POST'){
	echo '';
	$content = trim(file_get_contents("php://input"));
	$json_array = json_decode($content,true);
	//echo $json_array['array'][0]['Temperature'];
	$data = $json_array['array'];
	$hourFill = ":00:00";
	$query = "SELECT * FROM PlanDatabase";
	$result = mysqli_query($dbc,$query);
	echo mysqli_num_rows($result);
	if(mysqli_num_rows($result)!=0){
		foreach($data as $json){
                        $temperature = $json['Temperature'];
                        $hour = $json['Hour'] . $hourFill;
			$query = "UPDATE PlanDatabase SET TEMPERATURE = '$temperature',SET_DATE = CURDATE(), SET_HOUR = CURTIME() WHERE HOUR = '$hour'";
			mysqli_query($dbc,$query)
                                     or die('ERROR in query');

		}
	}
	else{	

		foreach($data as $json){

                    $temperature = $json['Temperature'];
                    $hour = $json['Hour'] . $hourFill;
                    $query = "INSERT INTO PlanDatabase(HOUR,TEMPERATURE,SET_DATE,SET_HOUR)".
                            "VALUES('$hour','$temperature',CURDATE(),CURTIME())";
                    mysqli_query($dbc,$query)
                                 or die('ERROR in query');

		}
	}
	mysqli_close($dbc);
	exit;
}
?>
