-- Regimen table

create table if not exists dtl_RegimenMap (
	ID int identity primary key,
	Visit_Pk int,
	RegimenType varchar,
)