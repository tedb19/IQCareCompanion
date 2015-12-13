-- This schema will be created in memory for the DAO tests

-- Visits table
create table if not exists ord_Visit (
	Visit_Id int primary key,
	Ptn_Pk int,
        VisitDate date,
);

-- Regimen table
create table if not exists dtl_RegimenMap (
	ID int identity primary key,
	Visit_Pk int,
	RegimenType varchar,
);

-- Patient Lab Orders table
create table if not exists ord_PatientLabOrder (
	LabID int primary key,
	Ptn_pk int,
	VisitId int,
);

-- Patient Lab Results table
create table if not exists dtl_PatientLabResults (
	LabID int primary key,
	LabTestID int,
	TestResults varchar,
        CreateDate date,
);

ALTER TABLE ord_PatientLabOrder ADD FOREIGN KEY (VisitId) REFERENCES ord_Visit(Visit_Id);

