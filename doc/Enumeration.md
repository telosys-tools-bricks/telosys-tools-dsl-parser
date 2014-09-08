Enumerations : syntax
---------------------

```
<Enumeration> : <Type> {
    <field> = <value>,
    ...
}
```

with :
- ```<Enumeration>``` : Enumeration name
- ```<Type>``` : Type of the values of this enumeration
- ```<field>``` : Field name
- ```<value>``` : Field value

### ```<Enumeration>``` : Enumeration name ###

Replace ```<Enumeration>``` by the name of the enumeration.

The file name of the enumeration must be the name of this enumeration + the file extension ```.enum```

### ```<Type>``` : Field value ###

Replace ```<Type>``` by the type of values, which can be :
- ```string``` : String values
- ```decimal``` : Decimal values
- ```integer``` : Integer values

**The type is not mandatory. If missing, the default type is ```integer```.**

### ```<field>``` : Field name ###

Replace ```<field>``` by the name of the field.

The fields are separated by a comma ','.

A field has only one value.

### ```<value>``` : Field value ###

Replace ```<value>``` by the field value.

Its type can be :
- Number value ```123```
- String value ```"value"```

