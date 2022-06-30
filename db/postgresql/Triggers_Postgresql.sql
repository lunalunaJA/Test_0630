/*
  트리거 함수
*/

-- FUNCTION: public.tr_issupervisor()

-- DROP FUNCTION public.tr_issupervisor();

CREATE FUNCTION public.tr_issupervisor()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
   REFRESH MATERIALIZED VIEW zapp_group_mv;
   REFRESH MATERIALIZED VIEW zapp_groupuser_mv;
   return NEW;
END;
$BODY$;

-- FUNCTION: public.tr_syncgroup()

-- DROP FUNCTION public.tr_syncgroup();

CREATE FUNCTION public.tr_syncgroup()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
   REFRESH MATERIALIZED VIEW zapp_group_mv;
   return NEW;
END;
$BODY$;

/*
 트리거
*/
create trigger tr_supervisor_del after
delete
    on
    public.zapp_dept for each row execute function tr_issupervisor();
create trigger tr_supervisor_ins after
insert
    on
    public.zapp_dept for each row execute function tr_issupervisor();
create trigger tr_syncgroup_edit after
update
    on
    public.zapp_dept for each row execute function tr_syncgroup();

create trigger tr_supervisor_del after
delete
    on
    public.zapp_deptuser for each row
    when ((old.issupervisor = 'Y'::bpchar)) execute function tr_issupervisor();
create trigger tr_supervisor_ins after
insert
    on
    public.zapp_deptuser for each row
    when ((new.issupervisor = 'Y'::bpchar)) execute function tr_issupervisor();
create trigger tr_supervisor_up after
update
    of issupervisor on
    public.zapp_deptuser for each row execute function tr_issupervisor();

create trigger tr_syncgroup_del after
delete
    on
    public.zapp_group for each row execute function tr_syncgroup();
create trigger tr_syncgroup_edit after
update
    on
    public.zapp_group for each row execute function tr_syncgroup();
create trigger tr_syncgroup_ins after
insert
    on
    public.zapp_group for each row execute function tr_syncgroup();

create trigger tr_supervisor_del after
delete
    on
    public.zapp_groupuser for each row execute function tr_issupervisor();
create trigger tr_supervisor_ins after
insert
    on
    public.zapp_groupuser for each row execute function tr_issupervisor();
