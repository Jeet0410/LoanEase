<div align="center">

# LoanEase

</div>

## Project Overview
LoanEase is a modular, CLI-based **loan amortization simulator** that empowers users to explore repayment schedules, what-if scenarios (extra payments, rate shocks), and export results in CSV/PDF formats. Built with a test-driven development approach, LoanEase emphasizes accuracy, reliability, and maintainability.

## Contributors
- [Jeet Patel](https://github.com/Jeet0410) 
- [Jay Patel](https://github.com/JAY0103) 
- [Rayansh Chowatia](https://github.com/Rayansh-Chowatia)  

## Documentation & Reports
- **Project Report:** [REPORT.md](/report.md)  
- **Testing Plan:** [TESTING.md](/testing.md)  
- **Project Presentation:** [Project Presentation](/Documents/Project-Presentation)  

## Getting Started
Clone the repository and navigate into the project directory:

```bash
# Clone the LoanEase repository
git clone https://github.com/Jeet0410/LoanEase.git
```
```bash
# Change into the cloned directory
cd LoanEase
```

## Build & Run the Application
1. **Build with Maven**
   ```bash
   mvn clean package
   ```
2. **Run the packaged JAR**
   ```bash
   java -jar target/loanease-1.0-SNAPSHOT.jar
   ```
3. **Follow the CLI prompts** to input loan parameters and choose export options.

## Run the Test Suite
### Option 1: Use the Testing Sidebar

1. Click the **Testing** icon (🧪) in the sidebar.
2. You’ll see your test class and test methods listed.
3. Right Click the **loanease** icon and choose 'Run Test with Coverage'.

### Option 2: Right-Click in Code

1. Inside any test method, right-click and choose:
   - **Run Test with coverage** or **Debug Test**

### Option 3: Command line  
1. If you want to use the command line then: 
    ```bash
    mvn test
    ```

   
VSCode will show test results in the **Test Output Panel**.
### Enjoy LoanEase!!