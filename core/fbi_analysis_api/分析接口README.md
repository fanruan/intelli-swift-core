##Hibernate: Criteria Queries in Depth 

In a previous discussion, the focus was HQL. Though HQL works with the object-oriented approach, the developer still needs to know SQL. The only difference is the replacement of the relational methodology with the object-oriented one. In essence, a developer still has to drop into an SQL-like syntax for CRUD operations.

To overcome this obstacle and provide an extensible as well as completely object-oriented solution, Hibernate exposes a set of APIs that map the SQL functionality to objects, eliminating the need for an SQL-like syntax. These APIs constitute the Hibernate Criteria Query APIs. In this discussion the focus will be on Criteria Query APIs, or Criteria Queries in short.

The first section will cover the core classes of Criteria Queries. In the second section, the core classes will be mapped to the core functionalities of SQL. In the third and last section I will rewrite the application developed in the discussion about HQL and extend its functionalities. That’s the agenda for this discussion.

##Hibernate: Criteria Queries in Depth - The Core Classes of Criteria Queries 


Each class of the Criteria Query API represents an aspect of the relational approach. There are five core APIs that are commonly used. They are:

* Criteria

2. Criterion

3. Restrictions

4. Projection

5. Order

The Criteria class provides the gateway to working with criteria APIs. It is an interface that provides a simplified API for retrieving objects containing or composed of Criterion objects. In a situation where the restrictions query is composed of a variable number of fields, this approach is very useful. To get a reference to the Criteria class, use the createCriteria() method of the Session class. This method takes the name of the ORM class on which the query has to be executed. In essence the Session acts as a factory for the Criteria class. In statement form it would be:

	Criteria criteria= session.createCriteria(Order.class)

The above statement returns a reference to Criteria for the Order ORM class.

Looking at the second core API, in the relational approach, conditions placed on retrieving data are known as criterion. The Criterion class is the object-oriented representation of the relational criterion. It can be used as restrictions on the criteria query. In other words, Criterion is the object-oriented representation of the "where" clause of a SQL query. The conditions to be applied (also known as restrictions) can be provided by the Restrictions class. In code this would be:

	Criterion crit=Restriction.eq(“orderId”,”OD00009”);
	criteria.add(crit);   

From the above example it is clear that the Criterion is the "where" clause, which, when added to the Criteria object, provides a complete query with restrictions. Here the built-in Restriction type eq() (for testing equality) is being used. 

The Restriction API provides the built-in types for Criterion. Essentially, the Restriction class is a factory to the Criterion class. All of its methods are static. In Hibernate 2.x, the Expression class provided the services that are now provided by the Restriction class. The Restriction class provides almost all the required restrictions such as equals (eq()), logical and (and()), like (like()) and so on. Here I have used the term ‘almost’ because some of the restrictions dealing with Grouping are provided by the Projection class.

The Projection class is an object-oriented representation of query resultset projection in a Criteria query. In simpler terms, projection refers to the fields mentioned in the select clause of a query. The same can be achieved by using the Projection class in a Criteria query. The Projection class acts as a factory for the Projection class. Projection can be added by using the addProjection() method of the ProjectionList class. The addProjection() method of the Criteria class in turn returns a Criterion object. In code this would be:

	List orders = session.createCriteria(Order.class)
     .setProjection( Projections.projectionList()
      .add( Projections.rowCount() )
      ).list();

The Order class represents the "order by" clause of SQL. By using the asc() and desc() methods of this class, order can be imposed upon the Criteria resultselt.

Those are the major core classes of Hibernate. Though this clears the air about participating classes, how they are really used is still a question. That is what I am going to discuss in next section.

##Hibernate: Criteria Queries in Depth - Using Criteria Queries 


Now that the common core classes have been introduced, their usage in applications can be discussed. I already said in the last article that in SQL, DML plays  the most important role in the R(retrieve) operation of the CRUD quad. This is reflected in the core classes of Criteria API. The retrieval of data itself can be separated into four major categories:

*  Projection

2. Restriction

3. Aggregation

4. Grouping
The usage of core classes among these categories cannot be generalized. The reason for this will be evident from the details. All the examples are based on the ORDER and PRODUCTS table.

Do not confuse the term "Projection" as it is used here with the Projection class. Projection in common terms means to retrieve, and in SQL it means the "Select" clause. The "Select" clause is just a part of the services provided by the Projection class. Following is SQL query for projection of all fields of the ORDER table in SQL:

	SELECT * FROM ORDER

The Criteria equivalent would be:

	List orders= session.createCriteria(Order.class).list();

The above statement executes the corresponding SQL statement at the database server, populates the instances of the Order ORM class, adds them to a list and returns the List object. Actually, the above statement is composed of two statements:  

	Criteria criteria= session.createCriteria(Order.class) 
 and  

	List orders=criteria.list().

The combination of such dependent statements is known as method chaining. From now on I will be using this technique extensively. The above code retrieves all the rows from the ORDER table. But what if only the data contained in one of the fields has to be retrieved, as in the following SQL query:

	SELECT NAME FROM PRODUCT

Here, the Projection class comes into play. The above query can be rewritten into a Criteria query as:

List products=session.createCriteria(Product.class)
     . setProjection(Projection.property(\"name\"))
     .list();

It is clear from the above example that to query based on just one field, the fieldname is passed as an argument to the property() method of the Projection class. The Projection instance returned in turn becomes an argument to the setProjection() method. Similarly, to retrieve data based on two fields, ProjectionList has to be used. Hence the SQL query:

	SELECT NAME, ID FROM PRODUCT

Would become

	List products =session.createCriteria(Product.class).setProjection(
    Projections.propertyList()
        .add(Projection.property(\"name\"))
        .add(Projection.property(\"id\"))
    )
    .list();

Now let's make the query more complex by introducing joins. What would be the equivalent of a query such as the one below:

	SELECT O.*, P.* FROM ORDERS O, PRODUCT P WHERE O.ORDER_ID=P.ORDER_ID;

If you think the Criteria representation of the above would be as complex, then have a look at the following:

	List orders = session.createCriteria(Order.class)
            .setFetchMode(“products”,FetchMode.JOIN)
            .list();

It's as simple as that. The only thing to be done is to call the setFetchMode() of the Criteria class with two parameters: the name of the class with which the current class has to be joined and mode of the fetching of the data from the associated class. In the above case, the class name is actually the instance variable provided within the Order class. The mode is Join.

So retrieval is done, but there is just one problem. If the data has to be retrieved based on a condition, then what? Then Restriction has to be used.

In layman’s terms, restriction means imposing conditions. To retrieve data based on certain conditions, Restriction must be used. Here the Restriction class comes into the picture. All the conditions provided by SQL are available in Criteria. The ones most commonly used are as follows:

	Restriction.between is used to apply a "between" constraint to the field.

	Restriction.eq is used to apply an "equal" constraint to the field.

	Restriction.ge is used to apply a "greater than or equal" constraint to the field.

	Restriction.gt is used to apply a "greater than" constraint to the field.
	
	Restriction.idEq is used to apply an "equal" constraint to the identifier property.
	
	Restriction.in is used to apply an "in" constraint to the field.
	
	Restriction.isNotNull is used to apply an "is not null" constraint to the field.  
	
	Restriction.isNull is used to apply an "is null" constraint to the field.
	
	Restriction.ne is used to apply a "not equal" constraint to the field.     

So a SQL such as this

     SELECT * FROM ORDERS WHERE ORDER_ID=’1092’;

Would become   

    List orders= session.createCriteria(Order.class)
               .add(Restrictions.eq(“orderId”,”1092”))
               .list();

Applying the restrictions becomes easy in the case of joins as well. For example, the following query

    SELECT O.*, P.* FROM ORDERS O, PRODUCT P WHERE

    O.ORDER_ID=P.ORDER_ID AND P.ID=’1111’;

Would become

	List orders = session.createCriteria(Order.class)
        .setFetchMode(“products”,FetchMode.JOIN)
        .add(Restrictions.eq(“id”,”1111”))
        .list();

Just adding the Restriction to Criteria returned by setFetchMode() does the same thing that the above given SQL does.

Through restriction conditions can be imposed on data retrieval, there are situations where the data to be retrieved has to be based on the groups of values of a column. In such conditions, Aggregation must be used. Criteria provides aggregation functionality through the Projection class itself. So to get the count of all the rows present in the ORDER table based on the ID field, the criteria query would be:

	List orders = session.createCriteria(Order.class)
     .setProjection( Projections.projectionList()
     .add( Projections.count(“id”) ))
     .list();

Similarly all the aggregate functions can be used as they are provided as static functions. As shown in the above example, each function takes the field name as the argument.

When the aggregation functions are used, the values may have to be grouped according to a particular field. Grouping always operates on a dataset. In Criteria Query API, grouping is provided by the Projection class. The groupProperty() method of the Projections class provides the grouping functionality. So a query like the one given below:

	SELECT COUNT(ID) FROM ORDER  HAVING PRICETOTAL>2000 GROUP BY ID

 Can be rewritten in Criteria query as follows:

	List orders = session.createCriteria(Order.class)
     .setProjection( Projections.projectionList()
      .add( Projections.count(“id”) )
       .add( Projections.groupProperty(“id”) )
     )
      .list();

That brings us to the end of this section. In the next section I will be using the Criteria query APIs to rewrite the application written in the last part.

##Hibernate: Criteria Queries in Depth - A Criteria Query in the Real World 

Now it is time to apply what we have discussed up till now. In this section I will be rewriting the application developed in the previous article (HQL in Depth). Almost all the code is same except the query part. So the details are as below:

package com.someorg.persist.op;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.someorg.persist.Order
import org.hibernate.*;
import org.hibernate.criterion.*;

public class OrderOP {

SessionFactory sf;

    public OrderOP() {
        Configuration cfg = new Configuration()
                .addClass(Order.class);

        sf = cfg.buildSessionFactory();

    }

    public Order getOrder(String lower, String upper) {
        // open session
        Session sess = sf.openSession();
        //The following code has been commented so that
        //comparison between HQL and Criteria Query

                /*String query = "select o from o "
               + "Order as o join o.products as p "
               + "where o.priceTotal > :priceTotalLower "
               + "and o.priceTotal < :priceTotalUpper";           

              Query q = sess.createQuery(query);
              q.setDouble("priceTotalLower", Double.parseDouble(lower));
              q.setDouble("priceTotalUpper",Double.parseDouble(upper));
                    */
        List list = sess.createCriteria(Order.class)
                .add(Restrictions.between(lower, upper)
                .list();
        Order o = (Order) list.iterator.next();
        return o;
    }

    public static void main(String args[]) {
        Order o = OrderOP().getOrder(“2000’,”3000”);
        System.out.println(“Order Id:”+o.id);
        //and so on
    }
}

You can see from the above code that if the object-oriented approach is really clear, then Criteria APIs are the easiest mode of data retrieval. That brings us to the end of this discussion. However, Hibernate is not just about CRUD operations. It is also about transaction and entity management as well as optimization techniques. These are topics that will be covered in the future.




#######Read more at http://www.devarticles.com/c/a/Java/Hibernate-Criteria-Queries-in-Depth/3/#OYxJDRpKZErTB0je.99


