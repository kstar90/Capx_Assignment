1 -> Frontend Development (React)
i)	Tools and Setup:
a)	Use React for the frontend.
b)	Install necessary packages:
Code: -
npx create-react-app portfolio-tracker
cd portfolio-tracker
npm install axios react-router-dom chart.js
ii)	Directory Structure:
Css code: -
src/
  components/
    Dashboard.js
    StockForm.js
    StockList.js
  pages/
    Home.js
    AddEditStock.js
  App.js
  index.js
iii)	Key Components:
a)	Dashboard.js
Displays portfolio metrics such as total value, top-performing stock, and portfolio distribution.
Jsx code: -
import React, { useState, useEffect } from "react";
import axios from "axios";

const Dashboard = () => {
    const [portfolioValue, setPortfolioValue] = useState(0);
    const [topStock, setTopStock] = useState("");
    const [distribution, setDistribution] = useState([]);

    useEffect(() => {
        axios.get("/api/stocks")
            .then((response) => {
                const stocks = response.data;
                let totalValue = 0;
                let topPerformance = { name: "", value: 0 };

                stocks.forEach((stock) => {
                    const value = stock.quantity * stock.currentPrice;
                    totalValue += value;
                    if (value > topPerformance.value) {
                        topPerformance = { name: stock.name, value };
                    }
                });

                setPortfolioValue(totalValue);
                setTopStock(topPerformance.name);
                setDistribution(stocks);
            });
    }, []);

    return (
        <div>
            <h1>Dashboard</h1>
            <p>Total Portfolio Value: ${portfolioValue}</p>
            <p>Top Performing Stock: {topStock}</p>
            {/* Add pie chart for distribution */}
        </div>
    );
};

export default Dashboard;
b)	StockForm.js
A form to add/edit stock details.
Jsx code: -
import React, { useState } from "react";
import axios from "axios";

const StockForm = ({ stock, onSave }) => {
    const [formData, setFormData] = useState(stock || { name: "", ticker: "", quantity: 1, buyPrice: 0 });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const endpoint = stock ? `/api/stocks/${stock.id}` : "/api/stocks";
        const method = stock ? "put" : "post";

        axios[method](endpoint, formData)
            .then(() => onSave())
            .catch(console.error);
    };

    return (
        <form onSubmit={handleSubmit}>
            <input type="text" name="name" placeholder="Stock Name" value={formData.name} onChange={handleChange} />
            <input type="text" name="ticker" placeholder="Ticker" value={formData.ticker} onChange={handleChange} />
            <input type="number" name="quantity" placeholder="Quantity" value={formData.quantity} onChange={handleChange} />
            <input type="number" name="buyPrice" placeholder="Buy Price" value={formData.buyPrice} onChange={handleChange} />
            <button type="submit">{stock ? "Update" : "Add"} Stock</button>
        </form>
    );
};

export default StockForm;
c)	StockList.js
Displays current stock holdings.
Jsx code: -
import React from "react";

const StockList = ({ stocks, onEdit, onDelete }) => {
    return (
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Ticker</th>
                    <th>Quantity</th>
                    <th>Buy Price</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {stocks.map((stock) => (
                    <tr key={stock.id}>
                        <td>{stock.name}</td>
                        <td>{stock.ticker}</td>
                        <td>{stock.quantity}</td>
                        <td>${stock.buyPrice}</td>
                        <td>
                            <button onClick={() => onEdit(stock)}>Edit</button>
                            <button onClick={() => onDelete(stock.id)}>Delete</button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default StockList;
2 -> Backend Development (Spring Boot)
i)	Tools and Setup:
a)	Use Spring Initializr to generate the project with dependencies:
	Spring Web
	Spring Data JPA
	MySQL Driver
ii)	Directory structure:
bash code: - 
src/main/
  java/com/example/portfolio/
    controller/
    service/
    model/
    repository/
  resources/
    application.properties
iii)	Configuration:
a)	application.properties
properties: -
code: -
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
iv)	Key Components:
a)	Stock Model
Java code: -
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ticker;
    private int quantity;
    private double buyPrice;

    // Getters and Setters
}
b)	Stock Repository
Java code: -
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
}


c)	Stock Service

Java code: -
@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock updateStock(Long id, Stock stockDetails) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.setName(stockDetails.getName());
        stock.setTicker(stockDetails.getTicker());
        stock.setQuantity(stockDetails.getQuantity());
        stock.setBuyPrice(stockDetails.getBuyPrice());
        return stockRepository.save(stock);
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }
}


v)	Stock Controller

Java code: -
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    @Autowired
    private StockService stockService;

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @PostMapping
    public Stock addStock(@RequestBody Stock stock) {
        return stockService.addStock(stock);
    }

    @PutMapping("/{id}")
    public Stock updateStock(@PathVariable Long id, @RequestBody Stock stockDetails) {
        return stockService.updateStock(id, stockDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
    }
}


3 -> Database Schema (MySQL)
i)	Schema for stocks table:
Sql command: -
CREATE TABLE stocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    ticker VARCHAR(10),
    quantity INT,
    buy_price DOUBLE
);
4 -> Real-Time Data Integration
Use Yahoo Finance API for real-time stock prices. Example:
Python code: -
import yfinance as yf
def get_stock_price(ticker):
    stock = yf.Ticker(ticker)
    return stock.history(period="1d")["Close"].iloc[-1]
5 -> Deployment
Frontend: Deploy on Vercel or Netlify.
Backend: Deploy on Heroku, AWS, or Render.
i)	Frontend Deployment: React on Netlify
a)	Steps:
a.1) Prepare the React Application for Deployment
	Build the production version of the app:
	Bash code: -
npm run build
	This will create a build directory containing the optimized static files.
b)	Create a Netlify Account
	Visit Netlify and create an account if you do not already have one.
c)	Deploy the React App
	After logging in, click on "Add new site" and choose "Deploy manually".
	Drag and drop the build folder generated in the previous step into the Netlify deployment area.
	Wait for the deployment to complete. Netlify will provide you with a live URL for your application.

d)	Configure the Frontend to Connect to Backend
	Ensure the React app makes API calls to the backend hosted on Heroku. For example, update your API base URL in a configuration file:
Javascript code: -
const API_BASE_URL = "https://your-heroku-backend-url/api";
export default API_BASE_URL;
	Update API calls in your React app to use this base URL.
ii)	Backend Deployment: Spring Boot on Heroku
a)	Steps:
a.1) Prepare the Spring Boot Application for Deployment
 Ensure your application.properties file uses environment variables for database credentials:
Properties: -
Code: -
spring.datasource.url=${JDBC_DATABASE_URL}
spring.datasource.username=${JDBC_DATABASE_USERNAME}
spring.datasource.password=${JDBC_DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
	Update the pom.xml to include a plugin for Heroku deployment:
Xml code: -
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
b)	Create a Heroku Account
	Visit Heroku and create an account if you do not already have one.
c)	Install Heroku CLI
	Download and install the Heroku CLI from here.
d)	Create a New Heroku Application
	Log in to Heroku via the CLI:

Bash code: -
heroku login
	Create a new app:
Bash code: -
heroku create your-app-name
e)	Configure a MySQL Add-on for Heroku
	Attach a free MySQL database to your Heroku app:
Bash code: -
heroku addons:create jawsdb:kitefin
	Retrieve the database credentials:
Bash code: -
heroku config
	Ensure the database URL (JDBC_DATABASE_URL) is used in your Spring Boot application.
f)	Deploy the Spring Boot App
	Initialize a Git repository in your project folder (if not already done):
bash code: -
git init
git add .
git commit -m "Initial commit"
	Add the Heroku remote:
Bash code: -
heroku git:remote -a your-app-name
	Deploy the app:
bash code: -
git push heroku main
g)	Test the Backend
	After deployment, Heroku will provide a URL for your backend (e.g., https://your-app-name.herokuapp.com).
	Test API endpoints using tools like Postman or curl:
bash code: -
curl https://your-app-name.herokuapp.com/api/stocks


h)	Final Integration
Once both frontend and backend are deployed:
	Update the frontend to use the live backend URL in the API calls.
	Test the React app to ensure it can fetch data from the backend and perform all CRUD operations.
i)	Common Issues & Fixes
	CORS Errors:
i.1) Add a CORS configuration in your Spring Boot app:
java code: -
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
	Environment Variable Setup:
Ensure you configure environment variables in Heroku for database credentials:
bash code: -
heroku config:set JDBC_DATABASE_URL=your_database_url
heroku config:set JDBC_DATABASE_USERNAME=your_database_username
heroku config:set JDBC_DATABASE_PASSWORD=your_database_password
j)	Deliverables:
	Frontend URL: A live Netlify URL (e.g., https://portfolio-tracker.netlify.app).
	Backend URL: A live Heroku URL (e.g., https://portfolio-tracker-backend.herokuapp.com).
