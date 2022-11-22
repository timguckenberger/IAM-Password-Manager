
    create table core_action (
       dc_id integer not null,
        action varchar(128) not null,
        moduleId varchar(64) not null,
        subject varchar(128) not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table core_group (
       dc_id integer not null,
        jpaVersion integer not null,
        description varchar(255),
        groupDn varchar(255),
        dc_name varchar(255) not null,
        dc_role integer,
        dc_ldap integer,
        primary key (dc_id)
    ) engine=InnoDB;

    create table core_ldap (
       dc_id integer not null,
        baseDN varchar(255) not null,
        configJson varchar(4096),
        domainType integer not null,
        enable bit not null,
        filter varchar(255) not null,
        firstNameAttribute varchar(255) not null,
        host varchar(255) not null,
        lastNameAttribute varchar(255) not null,
        loginAttribute varchar(255) not null,
        mailAttribute varchar(255),
        mapEmailDomains varchar(255),
        mobileAttribute varchar(255),
        name varchar(64) not null,
        password mediumblob not null,
        dc_rank integer,
        searchAccount varchar(255) not null,
        telephoneAttribute varchar(255),
        dc_version integer,
        primary key (dc_id)
    ) engine=InnoDB;

    create table core_ref_user_group (
       group_id integer not null,
        user_id integer not null
    ) engine=InnoDB;

    create table core_role (
       dc_id integer not null,
        disabled bit not null,
        jpaVersion integer not null,
        dc_name varchar(64) not null,
        dc_rank integer not null,
        systemRole bit not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table core_role_core_action (
       core_role_dc_id integer not null,
        actions_dc_id integer not null,
        primary key (core_role_dc_id, actions_dc_id)
    ) engine=InnoDB;

    create table core_seq (
       seq_name varchar(255) not null,
        seq_value bigint,
        primary key (seq_name)
    ) engine=InnoDB;

    insert into core_seq(seq_name, seq_value) values ('CORE_GROUP.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_absence.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_assignments.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_roster_user.ID',1);

    insert into core_seq(seq_name, seq_value) values ('ROLE.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_type.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_roster.ID',1);

    insert into core_seq(seq_name, seq_value) values ('LDAP.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_roster_shift.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_team.ID',1);

    insert into core_seq(seq_name, seq_value) values ('SEM_ACTION.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_entry.ID',1);

    insert into core_seq(seq_name, seq_value) values ('CORE_USER.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_shift.ID',1);

    insert into core_seq(seq_name, seq_value) values ('shifts_skills.ID',1);

    create table core_user (
       dc_id integer not null,
        acSuspendedTill datetime,
        disabled bit not null,
        displayName varchar(255),
        email varchar(255),
        failActivations integer not null,
        hashPassword mediumblob,
        hmac tinyblob not null,
        jpaVersion integer not null,
        locale integer,
        lastLogin datetime,
        loginId varchar(255) not null,
        mobileNumber varchar(255),
        objectGuid tinyblob,
        passCounter integer not null,
        privateEmail varchar(255),
        prvMobile varchar(32),
        dc_salt tinyblob,
        saveit mediumblob,
        dc_tel varchar(255),
        userDn varchar(255),
        userPrincipalName varchar(255),
        dc_role integer not null,
        userext integer,
        dc_ldap integer,
        primary key (dc_id)
    ) engine=InnoDB;

    create table core_userext (
       dc_userext_id integer not null,
        dc_country varchar(255),
        photo blob,
        dc_timezone varchar(255),
        primary key (dc_userext_id)
    ) engine=InnoDB;

    create table shifts_absence (
       dc_id integer not null,
        absenseType integer,
        comment varchar(255),
        currentDate date,
        endDate date,
        startDate date,
        dc_user integer not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_assignments (
       dc_id integer not null,
        duration float,
        endDate time,
        startDate time,
        shifts_user_id integer,
        shifts_shift_id integer,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_entry (
       dc_id integer not null,
        endDate date,
        resourcesRequired integer not null,
        startDate date,
        dc_team integer not null,
        dc_type integer not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_roster (
       dc_id integer not null,
        endDate datetime,
        rosterName varchar(255),
        rosterType integer,
        skipWeekends bit not null,
        startDate datetime,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_roster_shift (
       dc_id integer not null,
        numbering integer not null,
        resources integer not null,
        roster_shift integer not null,
        dc_team integer not null,
        dc_type integer not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_roster_shifts_roster_shift (
       ShiftsRosterEntity_dc_id integer not null,
        shiftsRosterTypeEntity_dc_id integer not null
    ) engine=InnoDB;

    create table shifts_roster_user (
       dc_id integer not null,
        dc_offset integer,
        dc_user integer not null,
        shifts_roster integer not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_shift (
       dc_id integer not null,
        comment varchar(255),
        ignoreWarning bit,
        shiftDate date,
        dc_shiftentry integer not null,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_ShiftUsers_Types (
       shiftsuser_id integer not null,
        shiftstype_id integer not null,
        primary key (shiftsuser_id, shiftstype_id)
    ) engine=InnoDB;

    create table shifts_skills (
       dc_id integer not null,
        skillAbbriviation varchar(255),
        skillName varchar(255),
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_team (
       dc_id integer not null,
        name varchar(255),
        dc_style varchar(255),
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_type (
       dc_id integer not null,
        colorCode varchar(255),
        duration float,
        endTime time,
        isOnCall bit,
        dc_name varchar(255),
        startTime time,
        dc_style varchar(255),
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_type_days (
       ShiftsTypeEntity_dc_id integer not null,
        workingDays varchar(255)
    ) engine=InnoDB;

    create table shifts_user (
       dc_id integer not null,
        availableOn date,
        exitDate date,
        dc_external bit not null,
        federalState varchar(255),
        onCallAllowed bit not null,
        onCallNumber varchar(255),
        userSettings varchar(4096),
        dc_user integer not null,
        dc_team integer,
        primary key (dc_id)
    ) engine=InnoDB;

    create table shifts_users_skills (
       shiftsuser_id integer not null,
        shiftsskills_id integer not null,
        primary key (shiftsuser_id, shiftsskills_id)
    ) engine=InnoDB;

    alter table core_action 
       add constraint UK_SEM_ACTION unique (moduleId, subject, action);

    alter table core_group 
       add constraint UK_APP_GROUP unique (dc_name);

    alter table core_ldap 
       add constraint UK_LDAP_NAME unique (name);

    alter table core_role 
       add constraint UK_ROLE_NAME unique (dc_name);

    alter table core_user 
       add constraint UK_APP_USER unique (loginId);

    alter table shifts_skills 
       add constraint UK_SKILL_ABBR_UNIQUE unique (skillAbbriviation);

    alter table shifts_skills 
       add constraint UK_SKILL_NAME_UNIQUE unique (skillName);

    alter table core_group 
       add constraint FK_GROUP_ROLE 
       foreign key (dc_role) 
       references core_role (dc_id);

    alter table core_group 
       add constraint FK_GROUP_LDAP 
       foreign key (dc_ldap) 
       references core_ldap (dc_id);

    alter table core_ref_user_group 
       add constraint FK_GROUP_USER 
       foreign key (user_id) 
       references core_user (dc_id);

    alter table core_ref_user_group 
       add constraint FK_USER_GROUP 
       foreign key (group_id) 
       references core_group (dc_id);

    alter table core_role_core_action 
       add constraint FK_ROLE_ACTION 
       foreign key (actions_dc_id) 
       references core_action (dc_id);

    alter table core_role_core_action 
       add constraint FKm8fcladhxpesfv9gs7r0leqqg 
       foreign key (core_role_dc_id) 
       references core_role (dc_id);

    alter table core_user 
       add constraint FK_USER_ROLE 
       foreign key (dc_role) 
       references core_role (dc_id);

    alter table core_user 
       add constraint FK_USER_EXTENSION 
       foreign key (userext) 
       references core_userext (dc_userext_id);

    alter table core_user 
       add constraint FK_USER_LDAP 
       foreign key (dc_ldap) 
       references core_ldap (dc_id);

    alter table shifts_absence 
       add constraint FK_SHIFTS_ABSENCE_USER 
       foreign key (dc_user) 
       references shifts_user (dc_id);

    alter table shifts_assignments 
       add constraint FK_SHIFTS_ID_USER 
       foreign key (shifts_user_id) 
       references shifts_user (dc_id);

    alter table shifts_assignments 
       add constraint FK_SHIFTS_ID_SHIFTS 
       foreign key (shifts_shift_id) 
       references shifts_shift (dc_id);

    alter table shifts_entry 
       add constraint FK_SHIFTS_TEAM 
       foreign key (dc_team) 
       references shifts_team (dc_id);

    alter table shifts_entry 
       add constraint FK_SHIFTS_TYPE 
       foreign key (dc_type) 
       references shifts_type (dc_id);

    alter table shifts_roster_shift 
       add constraint FK_SHIFTS_ROSTER_SHIFT 
       foreign key (roster_shift) 
       references shifts_roster (dc_id);

    alter table shifts_roster_shift 
       add constraint FK_SHIFTS_TEAM_ROSTER 
       foreign key (dc_team) 
       references shifts_team (dc_id);

    alter table shifts_roster_shift 
       add constraint FK_SHIFTS_TYPE_ROSTER 
       foreign key (dc_type) 
       references shifts_type (dc_id);

    alter table shifts_roster_shifts_roster_shift 
       add constraint FK9enfuq6l2blh36xm24vxse7yr 
       foreign key (shiftsRosterTypeEntity_dc_id) 
       references shifts_roster_shift (dc_id);

    alter table shifts_roster_shifts_roster_shift 
       add constraint FK_SHIFTS_ROSTER_TYPE 
       foreign key (ShiftsRosterEntity_dc_id) 
       references shifts_roster (dc_id);

    alter table shifts_roster_user 
       add constraint FK_SHIFTS_ROSTER_USER 
       foreign key (dc_user) 
       references shifts_user (dc_id);

    alter table shifts_roster_user 
       add constraint FK_SHIFTS_ROSTER 
       foreign key (shifts_roster) 
       references shifts_roster (dc_id);

    alter table shifts_shift 
       add constraint FK_SHIFTS_ENTRY_SHIFT 
       foreign key (dc_shiftentry) 
       references shifts_entry (dc_id);

    alter table shifts_ShiftUsers_Types 
       add constraint FK_SHIFTS_TYPE_USERS 
       foreign key (shiftstype_id) 
       references shifts_type (dc_id);

    alter table shifts_ShiftUsers_Types 
       add constraint FKm7tuhl6stntbyv10ed887nl1l 
       foreign key (shiftsuser_id) 
       references shifts_user (dc_id);

    alter table shifts_type_days 
       add constraint FK_SHIFTS_TYPE_DAYS 
       foreign key (ShiftsTypeEntity_dc_id) 
       references shifts_type (dc_id);

    alter table shifts_user 
       add constraint FK_SHIFTS_USER 
       foreign key (dc_user) 
       references core_user (dc_id);

    alter table shifts_user 
       add constraint FK_USER_TEAM 
       foreign key (dc_team) 
       references shifts_team (dc_id);

    alter table shifts_users_skills 
       add constraint FK_SHIFTS_SKILL_SKILLS 
       foreign key (shiftsskills_id) 
       references shifts_skills (dc_id);

    alter table shifts_users_skills 
       add constraint FK_SHIFTS_SKILL_SKILLS 
       foreign key (shiftsuser_id) 
       references shifts_user (dc_id);