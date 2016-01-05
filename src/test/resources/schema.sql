-- This schema will be created in memory for the DAO tests

-- Patient table
create table if not exists mst_Patient (
	Ptn_Pk int primary key,
        DOB date,
        CreateDate date,
        Sex int,
        MaritalStatus int,
        IQNumber varchar,
        PMTCTNumber varchar,
        firstname_decrypted varchar,
        middlename_decrypted varchar,
        lastname_decrypted varchar,
);

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

-- WHO Stage table
create table if not exists dtl_PatientStage (
	WHOStageID int primary key,
	WHOStage int,
	Visit_Pk int,
);

-- TB DIAGNOSIS table
create table if not exists dtl_PatientHivPrevCareIE (
	TBDiagnosisID int primary key,
	longTermTBStartDate date,
	Visit_pk int,
);

-- HIV CARE INITIATION table
create table if not exists Lnk_PatientProgramStart (
	ProgramStartID int primary key,
	StartDate date,
	ModuleId int,
        Ptn_pk int,
);

ALTER TABLE ord_PatientLabOrder ADD FOREIGN KEY (VisitId) REFERENCES ord_Visit(Visit_Id);
ALTER TABLE dtl_PatientStage ADD FOREIGN KEY (Visit_Pk) REFERENCES ord_Visit(Visit_Id);
ALTER TABLE dtl_PatientHivPrevCareIE ADD FOREIGN KEY (Visit_pk) REFERENCES ord_Visit(Visit_Id);

