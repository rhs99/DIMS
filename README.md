# Drug Information Management System(DIMS)
## Tech stack: Java, JavaFX, PostgreSQL
## Features:
- Getting all relevant informations (dosage, side effects, price etc.) about a medicine.
- Drugs can be searched by their generic name.
- User can search drugs by indications or symptoms.
- Analytics support for searching based on indications and medicines.
- User can buy medicines from pharmacies.
- User can search their nearest pharmacies for required amount of medicines.
- Pharmacies can manage their inventories.
- Pharmacies can buy medicines from manufacturers.

### DB Tables:
```
create table generic_drug
(
	id int primary key,
	name varchar(100),
	subclass_id int REFERENCES drug_subclass(id)	
);


create table indication
(
	id int primary key,
	name varchar(100)
);


create table company
(
	id int primary key,
	name varchar(100),
    income_from_selling float default 0
);


create table drug_class
(
 	id int primary key,
	name varchar(100)
);


create table drug_subclass
(
	id int primary key,
	drug_class_id int REFERENCES drug_class(id),
	name varchar(100)
);


create table medicine
(
	id int primary key,
	name varchar(100),
	medicine_type varchar(100),
	generic_id int REFERENCES generic_drug(id),
	weight varchar(100),
	company_id int REFERENCES company(id)
);


create table description
(
	medicine_id int references medicine(id),
	adult_dose varchar(1000),
	child_dose varchar(1000),
	renal_dose varchar(1000),
	side_effects varchar(1000),
	precautions_and_warnings varchar(1000),
	pack_size varchar(1000),
    unit_price float
);
 

create table cures
(
	generic_id int references generic_drug(id),
	indication_id int references indication(id) 
);


create table pharmacy
(
	id serial primary key,
	name varchar(50),
	pass varchar(50),
	longitude real,
	latitude real,
    income_from_selling float default 0           
);


create table client
(
	id serial primary key,
	name varchar(50),
	pass varchar(50),
	contact varchar(50)
);


create table buy
(
	id serial primary key,
	cid int references client(id),
	phid int references pharmacy(id),
	buy_date date
);


create table buy_medicine
(
	buy_id int references buy(id),
	med_id int references medicine(id),
	amount int	
);


create table purchase
(
	id serial primary key,
	phid int references pharmacy(id),
	com_id int references company(id),
    purchase_date date	
);


create table purchase_medicine
(
	purchase_id int references purchase(id),
	med_id int references medicine(id),
	amount int
);


create table stores
(
	ph_id int references pharmacy(id),
	med_id int references medicine(id),
	amount int 
);


create table med_src_cnt
(
	id int references medicine(id),
	cnt int default 0
);


create table indication_src_cnt
(
	id int references indication(id),
	cnt int default 0
);


create table answers
(
	name varchar(100)
);
```


### Stored Procedures:
#### Set up client buy: 
```
CREATE OR REPLACE FUNCTION set_up_client_buy(mid int,pid int,quantity int)
RETURNS TEXT AS
$$
DECLARE
    c int;
BEGIN
    c := 0;
    select count(*) into c
    from stores 
    where ph_id = pid and med_id = mid;
    if c = 0 then
	    return 'OUT OF STOCK';
    end if; 

    select amount into c
    from stores 
    where ph_id = pid and med_id = mid;

    if c < quantity then
	    return 'OUT OF STOCK';
    end if;

    update stores
    set amount  = amount - quantity
    where ph_id = pid and med_id = mid;

    return 'SUCCESSFUL';
END;
$$ 
LANGUAGE PLpgSQL;
```


#### Set up pharmacy buy:
```
CREATE OR REPLACE FUNCTION set_up_pharmacy_buy(mid int,pid int,quantity int)
RETURNS TEXT AS
$$
DECLARE
    c int;
BEGIN
    c := 0;
    select count(*) into c
    from stores 
    where ph_id = pid and med_id = mid;
    if c = 0 then
	    insert into stores values(pid,mid,quantity);
    else
	    update stores
	    set amount  = amount + quantity
	    where ph_id = pid and med_id = mid;
    end if; 
    return 'SUCCESSFUL';
END;
$$ 
LANGUAGE PLpgSQL;
```

#### Medicine search and report:
```
CREATE OR REPLACE FUNCTION medicine_search_report(mid int)
RETURNS TEXT AS
$$
DECLARE
c int;
BEGIN

c := 0;

select count(*) into c
from med_src_cnt 
where id = mid;

if c = 0 then
	insert into med_src_cnt values(mid,1);
else
	update med_src_cnt
	set cnt = cnt+1
	where id = mid;
	
end if; 

return 'SUCCESSFUL';

END;
$$ 
LANGUAGE PLpgSQL;
```


#### Indication search report:
```
CREATE OR REPLACE FUNCTION indication_search_report(i_name Text)
RETURNS TEXT AS
$$
DECLARE
    c int;
    iid int;
BEGIN
    c := 0;

    select id into iid
    from indication
    where name = i_name;

    select count(*) into c
    from indication_src_cnt 
    where id = iid;

    if c = 0 then
	    insert into indication_src_cnt values(iid,1);
    else
	    update indication_src_cnt
	    set cnt = cnt+1
	    where id = iid;
    end if;
    return 'SUCCESSFUL';
END;
$$ 
LANGUAGE PLpgSQL;
```

#### Fix client buy:
```
CREATE OR REPLACE FUNCTION fix_client_buy(uid int,pid int,mid int,quantity int)
RETURNS TABLE(RES TEXT) AS 
$$
DECLARE
    MSG TEXT;
    last_id int;
BEGIN
    MSG := set_up_client_buy(mid,pid,quantity);
    if(MSG = 'SUCCESSFUL') THEN
	    insert into buy(cid,phid,buy_date) values(uid,pid,current_date);
	    select MAX(id) into last_id from buy;
	    insert into buy_medicine values(last_id,mid,quantity);
	    RETURN QUERY SELECT NAME FROM ANSWERS WHERE NAME = 'SUCCESSFUL';
    else
        RETURN QUERY SELECT NAME FROM ANSWERS WHERE NAME = 'UNSUCCESSFUL';
    end if; 
END;
$$ 
LANGUAGE PLpgSQL;
```


#### Fix pharmacy buy:
```
CREATE OR REPLACE FUNCTION fix_pharmacy_buy(pid int,cid int,mid int,quantity int)
RETURNS TABLE(RES TEXT) AS 
$$
DECLARE
    last_id int;
    MSG TEXT;
BEGIN
    MSG := set_up_pharmacy_buy(mid,pid,quantity);

    insert into purchase(phid,com_id,purchase_date) values(pid,cid,current_date);
    select MAX(id) into last_id from purchase;
    insert into purchase_medicine values(last_id,mid,quantity);
    RETURN QUERY SELECT NAME FROM ANSWERS WHERE NAME = 'SUCCESSFUL';
END;
$$ 
LANGUAGE PLpgSQL;
```


#### Get nearest pharmacy:
```
CREATE OR REPLACE FUNCTION get_nearest_pharmacy(i_longitude real,i_latitude real,m_id int,quantity int) 
RETURNS TABLE(id int,dist real) AS
$$
DECLARE 
BEGIN
    RETURN QUERY (
	    select pp.id,sqrt(((i_longitude - pp.longitude)*(i_longitude - pp.longitude)) +
									((i_latitude - pp.latitude)*(i_latitude - pp.latitude)))
        from stores ss join pharmacy pp on ss.ph_id = pp.id
        where ss.med_id = m_id and ss.amount >= quantity
    );
END;
$$ 
LANGUAGE 'plpgsql';
```
 
#### Fix client sell:
```
CREATE OR REPLACE FUNCTION fix_client_sell(p_id int,c_id int,m_id int,quantity int)
RETURNS TABLE(RES TEXT) AS 
$$
DECLARE
    MSG TEXT;
    last_id int;
BEGIN

    MSG := set_up_client_buy(m_id,p_id,quantity);

    if(MSG = 'SUCCESSFUL') THEN
	    insert into buy(cid,phid,buy_date) values(c_id,p_id,current_date);
	    select MAX(id) into last_id from buy;
	    insert into buy_medicine values(last_id,m_id,quantity);
	    RETURN QUERY SELECT NAME FROM ANSWERS WHERE NAME = 'SUCCESSFUL';
    else
        RETURN QUERY SELECT NAME FROM ANSWERS WHERE NAME = 'UNSUCCESSFUL';
    end if; 
END;
$$ 
LANGUAGE PLpgSQL;
```

### TRIGGER : 
#### Set company income:
```
CREATE OR REPLACE FUNCTION set_company_income() RETURNS TRIGGER AS 
$$
DECLARE
    purchase_id int;
    c_id int;
    quant int;
    u_price float;

BEGIN
    purchase_id := new.purchase_id;
    quant := new.amount;
   
    select unit_price into u_price
    from description
    where medicine_id = new.med_id;

    select com_id into c_id
    from purchase
    where id = purchase_id;

    update company
    set  income_from_selling =  income_from_selling + (u_price*quant)
    where id = c_id;
    RETURN NEW;  
END;
$$
LANGUAGE plpgsql;


CREATE TRIGGER company_income_trigger AFTER INSERT ON purchase_medicine
FOR EACH ROW EXECUTE PROCEDURE set_company_income();
```


#### Set pharmacy income:
```
CREATE OR REPLACE FUNCTION set_pharmacy_income() RETURNS TRIGGER AS 
$$
DECLARE
    buy_id int;
    ph_id int;
    quant int;
    u_price float;
BEGIN
    buy_id := new.buy_id;
    quant := new.amount;

    select unit_price into u_price
    from description
    where medicine_id = new.med_id;
   
    select phid into ph_id
    from buy
    where id = buy_id;

    update pharmacy
    set  income_from_selling =  income_from_selling + (u_price*quant)
    where id = ph_id;
    RETURN NEW;  
END;
$$
LANGUAGE plpgsql;


CREATE TRIGGER pharmacy_income_trigger AFTER INSERT ON buy_medicine
FOR EACH ROW EXECUTE PROCEDURE set_pharmacy_income();
```
