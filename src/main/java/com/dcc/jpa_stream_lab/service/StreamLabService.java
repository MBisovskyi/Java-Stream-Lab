package com.dcc.jpa_stream_lab.service;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dcc.jpa_stream_lab.repository.ProductsRepository;
import com.dcc.jpa_stream_lab.repository.RolesRepository;
import com.dcc.jpa_stream_lab.repository.ShoppingcartItemRepository;
import com.dcc.jpa_stream_lab.repository.UsersRepository;
import com.dcc.jpa_stream_lab.models.Product;
import com.dcc.jpa_stream_lab.models.Role;
import com.dcc.jpa_stream_lab.models.ShoppingcartItem;
import com.dcc.jpa_stream_lab.models.User;
import org.springframework.web.bind.annotation.ResponseStatus;

@Transactional
@Service
public class StreamLabService {
	
	@Autowired
	private ProductsRepository products;
	@Autowired
	private RolesRepository roles;
	@Autowired
	private UsersRepository users;
	@Autowired
	private ShoppingcartItemRepository shoppingcartitems;


    // <><><><><><><><> R Actions (Read) <><><><><><><><><>

    public List<User> RDemoOne() {
    	// This query will return all the users from the User table.
    	return users.findAll().stream().toList();
    }

    public long RProblemOne()
    {
        // Return the COUNT of all the users from the User table.
        // You MUST use a .stream(), don't listen to the squiggle here!
        // Remember yellow squiggles are warnings and can be ignored.
    	return users.count();
    }

    public List<Product> RDemoTwo()
    {
        // This query will get each product whose price is greater than $150.
    	return products.findAll().stream().filter(price -> price.getPrice() > 150).toList();
    }

    public List<Product> RProblemTwo()
    {
        // Write a query that gets each product whose price is less than or equal to $100.
        // Return the list
        return products.findAll().stream().filter(price -> price.getPrice() <= 100).toList();
    }

    public List<Product> RProblemThree()
    {
        // Write a query that gets each product that CONTAINS an "s" in the products name.
        // Return the list
    	return products.findAll().stream().filter(name -> name.getName().contains("s")).toList();
    }

    public List<User> RProblemFour() {
        // Write a query that gets all the users who registered BEFORE 2016
        // Return the list
        // Research 'java create specific date' and 'java compare dates'
        // You may need to use the helper classes imported above!
        long filterDate = new GregorianCalendar(2016, Calendar.JANUARY, 1).getTimeInMillis();
        return users.findAll().stream().filter(date -> (date.getRegistrationDate().getTime() < filterDate)).toList();
    }

    public List<User> RProblemFive()
    {
        // Write a query that gets all of the users who registered AFTER 2016 and BEFORE 2018
        // Return the list
        long afterYear = new GregorianCalendar(2016, Calendar.JANUARY, 1).getTimeInMillis();
        long beforeYear = new GregorianCalendar(2018, Calendar.JANUARY, 1).getTimeInMillis();
        return users.findAll().stream().filter(date -> (date.getRegistrationDate().getTime() < beforeYear && date.getRegistrationDate().getTime() > afterYear)).toList();
    }

    // <><><><><><><><> R Actions (Read) with Foreign Keys <><><><><><><><><>

    public List<User> RDemoThree()
    {
        // Write a query that retrieves all of the users who are assigned to the role of Customer.
    	Role customerRole = roles.findAll().stream().filter(r -> r.getName().equals("Customer")).findFirst().orElse(null);
        return users.findAll().stream().filter(u -> u.getRoles().contains(customerRole)).toList();
    }

    public List<Product> RProblemSix()
    {
        // Write a query that retrieves all of the products in the shopping cart of the user who has the email "afton@gmail.com".
        // Return the list
        User filteredUser = (users.findAll().stream().filter(user -> user.getEmail().equals("afton@gmail.com")).findFirst().orElse(null));
        List <ShoppingcartItem> userProducts = shoppingcartitems.findAll().stream().filter(cart -> cart.getUser().equals(filteredUser)).toList();
        return userProducts.stream().map(ShoppingcartItem::getProduct).toList();
    }

    public long RProblemSeven()
    {
        // Write a query that retrieves all of the products in the shopping cart of the user who has the email "oda@gmail.com" and returns the sum of all of the products prices.
    	// Remember to break the problem down and take it one step at a time!
        User filteredUser = (users.findAll().stream().filter(user -> user.getEmail().equals("oda@gmail.com")).findFirst().orElse(null));
        List <ShoppingcartItem> userShoppingCart = shoppingcartitems.findAll().stream().filter(cart -> cart.getUser().equals(filteredUser)).toList();
        List <Product> userProducts = userShoppingCart.stream().map(ShoppingcartItem::getProduct).toList();
        List<Integer> userProductsPrices = userProducts.stream().map(Product::getPrice).toList();
        return userProductsPrices.stream().reduce(0, Integer::sum);

    }

    public List<Product> RProblemEight()
    {
        // Write a query that retrieves all of the products in the shopping cart of users who have the role of "Employee".
    	// Return the list
        Role userRole = roles.findAll().stream().filter(role -> role.getName().equals("Employee")).findFirst().orElse(null);
//        List<User> employees = users.findAll().stream().filter(user -> user.getRoles().contains(userRole)).toList();
        List<ShoppingcartItem> employeesShoppingCarts = shoppingcartitems.findAll().stream().filter(cart -> cart.getUser().getRoles().contains(userRole)).toList();
    	return employeesShoppingCarts.stream().map(ShoppingcartItem::getProduct).toList();
    }

    // <><><><><><><><> CUD (Create, Update, Delete) Actions <><><><><><><><><>

    // <><> C Actions (Create) <><>

    public User CDemoOne()
    {
        // Create a new User object and add that user to the Users table.
        User newUser = new User();        
        newUser.setEmail("mykola@gmail.com");
        newUser.setPassword("MykolaPass123");
        users.save(newUser);
        return newUser;
    }

    public Product CProblemOne()
    {
        // Create a new Product object and add that product to the Products table.
        // Return the product
    	Product newProduct = new Product();
        newProduct.setName("Epson XP2105 Printer");
        newProduct.setDescription("Used Printer in a good shape! Uses ink cartridges type 220!");
        newProduct.setPrice(150);
        products.save(newProduct);

    	return newProduct;

    }

    public List<Role> CDemoTwo()
    {
        // Add the role of "Customer" to the user we just created in the UserRoles junction table.
    	Role customerRole = roles.findAll().stream().filter(r -> r.getName().equals("Customer")).findFirst().orElse(null);
    	User david = users.findAll().stream().filter(u -> u.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
    	david.addRole(customerRole);
    	return david.getRoles();
    }

    public ShoppingcartItem CProblemTwo()
    {
    	// Create a new ShoppingCartItem to represent the new product you created being added to the new User you created's shopping cart.
        // Add the product you created to the user we created in the ShoppingCart junction table.
        // Return the ShoppingcartItem
        User userDavid = users.findAll().stream().filter(user -> user.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
        Product userDavidProduct = products.findAll().stream().filter(product -> product.getName().contains("Epson")).findFirst().orElse(null);
        ShoppingcartItem userDavidShoppingCart = new ShoppingcartItem();
        userDavidShoppingCart.setProduct(userDavidProduct);
        userDavidShoppingCart.setUser(userDavid);
        userDavidShoppingCart.setQuantity(+1);
        shoppingcartitems.save(userDavidShoppingCart);
    	return userDavidShoppingCart;
    	
    }

    // <><> U Actions (Update) <><>

    public User UDemoOne()
    {
         //Update the email of the user we created in problem 11 to "mike@gmail.com"
          User user = users.findAll().stream().filter(u -> u.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
          user.setEmail("mike@gmail.com");
          return user;
    }

    public Product UProblemOne()
    {
        // Update the price of the product you created to a different value.
        // Return the updated product
        Product updateProduct = products.findAll().stream().filter(product -> product.getName().contains("Epson")).findFirst().orElse(null);
        updateProduct.setPrice(149);
    	return updateProduct;
    }

    public User UProblemTwo()
    {
        // Change the role of the user we created to "Employee"
        // HINT: You need to delete the existing role relationship and then create a new UserRole object and add it to the UserRoles table
        Role customer = (roles.findAll().stream().filter(role -> role.getName().equals("Customer")).findFirst().orElse(null));
        Role employee = roles.findAll().stream().filter(role -> role.getName().equals("Employee")).findFirst().orElse(null);
        User david = users.findAll().stream().filter(user -> user.getEmail().equals("david@gmail.com")).findFirst().orElse(null);
        david.removeRole(customer);
        david.addRole(employee);

    	return david;
    }

    //BONUS:
    // <><> D Actions (Delete) <><>

    // For these bonus problems, you will also need to create their associated routes in the Controller file!
    
	// DProblemOne
    // Delete the role relationship from the user who has the email "oda@gmail.com".
    public String DProblemOne()
    {
        User getUser = users.findAll().stream().filter(user -> user.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        Role userRole = roles.findAll().stream().filter(role -> role.getUsers().contains(getUser)).findFirst().orElse(null);
        getUser.removeRole(userRole);
        return "Role is removed";
    }

    // DProblemTwo
    // Delete all the product relationships to the user with the email "oda@gmail.com" in the ShoppingCart table.
    public String DProblemTwo() {
        User filteredUser = users.findAll().stream().filter(user -> user.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        List<ShoppingcartItem> userShoppingCarts = filteredUser.getShoppingcartItems();
        List<Integer> userCartsIds = userShoppingCarts.stream().map(ShoppingcartItem::getId).toList();
        for (int i = 0; i < userShoppingCarts.size(); i++) {
            int cartId = userCartsIds.get(i);
            shoppingcartitems.deleteById(cartId);
        }
        return "Successfully removed";
    }

    // DProblemThree
    // Delete the user with the email "oda@gmail.com" from the Users table.
    public String DProblemThree() {
        User filteredUser = users.findAll().stream().filter(user -> user.getEmail().equals("oda@gmail.com")).findFirst().orElse(null);
        int userId = filteredUser.getId();
        users.deleteById(userId);
        return "User removed";
    }

}
