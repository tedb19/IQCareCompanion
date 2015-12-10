### IQCareCompanion [![Build Status](https://travis-ci.org/tedb19/IQCareCompanion.svg)](https://travis-ci.org/tedb19/IQCareCompanion) [![Coverage Status](https://coveralls.io/repos/tedb19/IQCareCompanion/badge.svg?branch=master&service=github)](https://coveralls.io/github/tedb19/IQCareCompanion?branch=master)

A HIV Case Based Surveillance System companion for [IQCare] (https://fgiqcare.codeplex.com/). The companion generates hl7 messages
 for any **sentinel event** of interest registered in the EMR, in a real-time fashion.

**Sentinel events** are a collection of indicators to be tracked during the surveillance period. They could include the following:
- date of HIV Diagnosis
- date of initiation to CARE
- CD4 Count
- WHO Stage etc

The sentinel events are described in the **events.json** file. You can add/remove any event from this list.

To mine all the sentinel events within the EMR, simply reset the values in **runtime.properties** to 0.