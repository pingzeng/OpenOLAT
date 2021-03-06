create table o_gta_mark (
  id number(20) generated always as identity,
  creationdate date not null,
  lastmodified date not null,
  fk_tasklist_id number(20) not null,
  fk_marker_identity_id number(20) not null,
  fk_participant_identity_id number(20) not null,
  primary key (id)
);

alter table o_gta_mark add constraint gtamark_tasklist_idx foreign key (fk_tasklist_id) references o_gta_task_list (id);
create index idx_gtamark_tasklist_idx on o_gta_mark (fk_tasklist_id);

-- temporary key
alter table o_temporarykey add column fk_identity_id number(20);

create index idx_tempkey_identity_idx on o_temporarykey (fk_identity_id);


-- taxonomy
create table o_tax_taxonomy (
  id number(20) generated always as identity,
  creationdate date not null,
  lastmodified date not null,
  t_identifier varchar2(64 char),
  t_displayname varchar2(255 char) not null,
  t_description CLOB,
  t_external_id varchar2(64 char),
  t_managed_flags varchar2(255 char),
  t_directory_path varchar2(255 char),
  t_directory_lost_found_path varchar2(255 char),
  fk_group number(20) not null,
  primary key (id)
);

alter table o_tax_taxonomy add constraint tax_to_group_idx foreign key (fk_group) references o_bs_group (id);
create index idx_tax_to_group_idx on o_tax_taxonomy (fk_group);


create table o_tax_taxonomy_level_type (
  id number(20) generated always as identity,
  creationdate date not null,
  lastmodified date not null,
  t_identifier varchar2(64 char),
  t_displayname varchar2(255 char) not null,
  t_description CLOB,
  t_external_id varchar2(64 char),
  t_managed_flags varchar2(255 char),
  t_css_class varchar2(64 char),
  t_visible number default 1,
  t_library_docs number default 1,
  t_library_manage number default 1,
  t_library_teach_read number default 1,
  t_library_teach_readlevels number(20) default 0 not null,
  t_library_teach_write number default 0,
  t_library_have_read number default 1,
  t_library_target_read number default 1,
  fk_taxonomy number(20) not null,
  primary key (id)
);

alter table o_tax_taxonomy_level_type add constraint tax_type_to_taxonomy_idx foreign key (fk_taxonomy) references o_tax_taxonomy (id);
create index idx_tax_type_to_taxonomy_idx on o_tax_taxonomy_level_type (fk_taxonomy);


create table o_tax_taxonomy_type_to_type (
  id number(20) generated always as identity,
  fk_type number(20) not null,
  fk_allowed_sub_type number(20) not null,
  primary key (id)
);

alter table o_tax_taxonomy_type_to_type add constraint tax_type_to_type_idx foreign key (fk_type) references o_tax_taxonomy_level_type (id);
create index idx_tax_type_to_type_idx on o_tax_taxonomy_type_to_type (fk_type);
alter table o_tax_taxonomy_type_to_type add constraint tax_type_to_sub_type_idx foreign key (fk_allowed_sub_type) references o_tax_taxonomy_level_type (id);
create index idx_tax_type_to_sub_type_idx on o_tax_taxonomy_type_to_type (fk_allowed_sub_type);


create table o_tax_taxonomy_level (
  id number(20) generated always as identity,
  creationdate date not null,
  lastmodified date not null,
  t_identifier varchar2(64 char),
  t_displayname varchar2(255 char) not null,
  t_description CLOB,
  t_external_id varchar2(64 char),
  t_sort_order number(20),
  t_directory_path varchar2(255 char),
  t_m_path_keys varchar2(255 char),
  t_m_path_identifiers varchar2(1024 char),
  t_enabled number default 1,
  t_managed_flags varchar2(255 char),
  fk_taxonomy number(20) not null,
  fk_parent number(20),
  fk_type number(20),
  primary key (id)
);

alter table o_tax_taxonomy_level add constraint tax_level_to_taxonomy_idx foreign key (fk_taxonomy) references o_tax_taxonomy (id);
create index idx_tax_level_to_taxonomy_idx on o_tax_taxonomy_level (fk_taxonomy);
alter table o_tax_taxonomy_level add constraint tax_level_to_tax_level_idx foreign key (fk_parent) references o_tax_taxonomy_level (id);
create index idx_tax_level_to_tax_level_idx on o_tax_taxonomy_level (fk_parent);
alter table o_tax_taxonomy_level add constraint tax_level_to_type_idx foreign key (fk_type) references o_tax_taxonomy_level_type (id);
create index idx_tax_level_to_type_idx on o_tax_taxonomy_level (fk_type);


create table o_tax_taxonomy_competence (
  id number(20) generated always as identity,
  creationdate date not null,
  lastmodified date not null,
  t_type varchar2(16),
  t_achievement decimal default null,
  t_reliability decimal default null,
  t_expiration_date date,
  t_external_id varchar2(64 char),
  t_source_text varchar2(255 char),
  t_source_url varchar2(255 char),
  fk_level number(20) not null,
  fk_identity number(20) not null,
  primary key (id)
);

alter table o_tax_taxonomy_competence add constraint tax_comp_to_tax_level_idx foreign key (fk_level) references o_tax_taxonomy_level (id);
create index idx_tax_comp_to_tax_level_idx on o_tax_taxonomy_competence (fk_level);
alter table o_tax_taxonomy_competence add constraint tax_level_to_ident_idx foreign key (fk_identity) references o_bs_identity (id);
create index idx_tax_level_to_ident_idx on o_tax_taxonomy_competence (fk_identity);


create table o_tax_competence_audit_log (
  id number(20) generated always as identity,
  creationdate date not null,
  t_action varchar2(32 char),
  t_val_before CLOB,
  t_val_after CLOB,
  t_message CLOB,
  fk_taxonomy number(20),
  fk_taxonomy_competence number(20),
  fk_identity number(20),
  fk_author number(20),
  primary key (id)
);


-- qpool
alter table o_qp_item add fk_taxonomy_level_v2 number(20);

alter table o_qp_item add constraint idx_qp_pool_2_tax_id foreign key (fk_taxonomy_level_v2) references o_tax_taxonomy_level(id);
create index idx_item_taxlon_idx on o_qp_item (fk_taxonomy_level_v2);

alter table o_qp_item drop constraint idx_qp_pool_2_field_id;
drop index idx_item_taxon_idx;





