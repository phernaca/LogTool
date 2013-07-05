
To use the Log Analyser Tool you must update the configuration file (csv_import.properties). The following properties are Mandadory

	csv_import_cla : in this property you must set the key for the Palma Loader (csvmapfile.txt) that you want to analyse
					e.g. if loading for Parts then the action is CreateTASInternalHW
	
	csv_import_id : this property must contain the full path to the directory where they are the input csv files previously loaded
					e.g. /home/wcadmin/MIG/PART1
	
	csv_import_wt : in this property you must set the $WT_HOME of the Windchill instance used during the load
					e.g. /opt/ptc/Windchill
	
	csv_import_vm : this property must contain the jvm id of the MethodServer that has performed the load of the action. 
					You can find this id in places like the MS logs or the failure file (CreateTASInternalHW-1836.log or CreateTASInternalHW-1836.failures)
					e.g. 1836

	csv_import_old : in this property you must set the full path of the directory that will contain the results of the analysis for the failed lines
					e.g. /home/wcadmin/LogAnalyser/out
					
					
These other properties are Optional and they have a default value by the code :

	csv_import_oc : this property tells if the resulting file of the analysis should be merged (option 'yes') with a main file that gathers the contents 
					of all results for this action. Default option in the code is 'no'.
	
	
	csv_import_dt : this property tells if the failure file to be analyzed must be taken into account only if it has been created before a given interval
					which is determined by the entry 'logs_migration.failure.cutoffDelta' in file 'logs_migration.properties'. Default option is 'yes'.