-- ====================================================================
-- ApexBuild Construction Management System
-- Master Database Schema (Microsoft SQL Server / SSMS)
-- Standard SQL format based on SLIIT Y2S1 Database Design & Development
-- ====================================================================

-- 1. Create and use Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'ConstructionDB')
BEGIN
    CREATE DATABASE ConstructionDB;
END
GO

USE ConstructionDB;
GO

-- 2. Drop existing tables in safe foreign key dependency order
DROP TABLE IF EXISTS quotations;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS purchase_requests;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS material_issues;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS daily_progress_logs;
DROP TABLE IF EXISTS inquiries;
DROP TABLE IF EXISTS project_tasks;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;
GO

-- ====================================================================
-- 1. Users Table (Authentication & System Roles)
-- ====================================================================
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- ADMIN, PROJECT_MANAGER, SITE_ENGINEER, STOREKEEPER, PROCUREMENT_OFFICER, FINANCE_OFFICER, CLIENT
    phone VARCHAR(50),
    address VARCHAR(255),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 2. Projects Table (Master Construction Projects)
-- ====================================================================
CREATE TABLE projects (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_name VARCHAR(200) NOT NULL,
    description VARCHAR(MAX),
    location VARCHAR(200) NOT NULL,
    client_id BIGINT,
    client_name VARCHAR(150),
    start_date DATE,
    end_date DATE,
    estimated_budget DECIMAL(18,2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'PLANNED', -- PLANNED, IN_PROGRESS, ON_HOLD, COMPLETED
    house_plan_file VARCHAR(255),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 3. Project Tasks Table (Site Construction Tasks)
-- ====================================================================
CREATE TABLE project_tasks (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT NOT NULL FOREIGN KEY REFERENCES projects(id) ON DELETE CASCADE,
    task_name VARCHAR(200) NOT NULL,
    description VARCHAR(MAX),
    assigned_worker VARCHAR(150),
    priority VARCHAR(50) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    status VARCHAR(50) DEFAULT 'TODO', -- TODO, IN_PROGRESS, COMPLETED
    due_date DATE,
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 4. Inquiries Table (Client Support & Inquiries)
-- ====================================================================
CREATE TABLE inquiries (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    client_id BIGINT,
    client_name VARCHAR(150) NOT NULL,
    client_email VARCHAR(150) NOT NULL,
    project_id BIGINT,
    subject VARCHAR(200) NOT NULL,
    message VARCHAR(MAX) NOT NULL,
    resolution VARCHAR(MAX),
    status VARCHAR(50) DEFAULT 'OPEN', -- OPEN, INVESTIGATING, RESOLVED
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 5. Daily Site Progress Logs Table
-- ====================================================================
CREATE TABLE daily_progress_logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT NOT NULL FOREIGN KEY REFERENCES projects(id) ON DELETE CASCADE,
    reported_by VARCHAR(150) NOT NULL,
    log_date DATE NOT NULL,
    milestone VARCHAR(200) NOT NULL,
    work_done VARCHAR(MAX) NOT NULL,
    issues_faced VARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 6. Materials Table (Central Warehouse Inventory)
-- ====================================================================
CREATE TABLE materials (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    material_name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL, -- CEMENT, STEEL, SAND, BRICKS, ELECTRICAL, PLUMBING, OTHER
    unit VARCHAR(50) NOT NULL, -- Bags, Tons, Cubes, Units, Meters
    unit_price DECIMAL(18,2) NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 10,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 7. Material Issues Table (Site Stock Disbursements)
-- ====================================================================
CREATE TABLE material_issues (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    material_id BIGINT NOT NULL FOREIGN KEY REFERENCES materials(id) ON DELETE CASCADE,
    project_id BIGINT FOREIGN KEY REFERENCES projects(id) ON DELETE SET NULL,
    issued_to VARCHAR(150) NOT NULL,
    quantity_issued INT NOT NULL,
    issue_date DATE NOT NULL,
    notes VARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 8. Suppliers Table (Approved Vendor Directory)
-- ====================================================================
CREATE TABLE suppliers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(150) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL,
    address VARCHAR(255),
    supply_category VARCHAR(150) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, BLACKLISTED
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 9. Purchase Requests Table (Material Purchase Orders)
-- ====================================================================
CREATE TABLE purchase_requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    supplier_id BIGINT FOREIGN KEY REFERENCES suppliers(id) ON DELETE SET NULL,
    material_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    estimated_cost DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, DELIVERED
    request_date DATE NOT NULL,
    notes VARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 10. Expenses Table (Site Expenditure Ledger)
-- ====================================================================
CREATE TABLE expenses (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT NOT NULL FOREIGN KEY REFERENCES projects(id) ON DELETE CASCADE,
    expense_title VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL, -- LABOR, MATERIALS, EQUIPMENT, TRANSPORT, UTILITIES, MISC
    amount DECIMAL(18,2) NOT NULL,
    expense_date DATE NOT NULL,
    receipt_path VARCHAR(255),
    description VARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- 11. Quotations Table (Official Client Cost Quotations)
-- ====================================================================
CREATE TABLE quotations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    project_id BIGINT FOREIGN KEY REFERENCES projects(id) ON DELETE SET NULL,
    client_name VARCHAR(150) NOT NULL,
    client_email VARCHAR(150) NOT NULL,
    project_name VARCHAR(200) NOT NULL,
    labor_cost DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    material_cost DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    overhead_cost DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, SENT, ACCEPTED, REJECTED
    generated_date DATE NOT NULL,
    created_at DATETIME DEFAULT GETDATE()
);
GO

-- ====================================================================
-- Initial Seed Data Insertion
-- ====================================================================

-- 1. Staff and Client Accounts
INSERT INTO users (full_name, email, password, role, phone, address) VALUES
('System Administrator', 'admin@construction.com', 'admin123', 'ADMIN', '0771234567', 'Headquarters, Colombo'),
('S.D.R. Wijesekera', 'pm@construction.com', '1234', 'PROJECT_MANAGER', '0712345678', 'Colombo, Sri Lanka'),
('Eng. H. Weerawansha', 'engineer@construction.com', '1234', 'SITE_ENGINEER', '0723456789', 'Kandy, Sri Lanka'),
('R.A.H.G.D. Bandara', 'store@construction.com', '1234', 'STOREKEEPER', '0754567890', 'Gampaha, Sri Lanka'),
('S. Vaishnavy', 'procure@construction.com', '1234', 'PROCUREMENT_OFFICER', '0765678901', 'Jaffna, Sri Lanka'),
('S.B.G. Pahanmi', 'finance@construction.com', '1234', 'FINANCE_OFFICER', '0786789012', 'Kurunegala, Sri Lanka'),
('Nimal Silva', 'client@gmail.com', '1234', 'CLIENT', '0779998877', 'Rajagiriya, Sri Lanka');
GO

-- 2. Projects
INSERT INTO projects (project_name, description, location, client_id, client_name, start_date, end_date, estimated_budget, status) VALUES
('Luxury Villa Project - Rajagiriya', 'Two-story luxury residential house with swimming pool and landscaped garden.', 'Rajagiriya, Colombo', 7, 'Nimal Silva', '2026-03-01', '2026-12-31', 25000000.00, 'IN_PROGRESS'),
('Commercial Complex Phase 1', 'Modern 4-story retail and office commercial space with basement parking.', 'Nawala Road, Nugegoda', 7, 'Nimal Silva', '2026-05-15', '2027-04-30', 65000000.00, 'PLANNED');
GO

-- 3. Tasks
INSERT INTO project_tasks (project_id, task_name, description, assigned_worker, priority, status, due_date) VALUES
(1, 'Foundation Excavation', 'Excavate earth up to 2.5 meters depth for column footings.', 'Ground Crew A (Lead: Sunil)', 'HIGH', 'COMPLETED', '2026-03-20'),
(1, 'Reinforced Concrete Columns', 'Erect and pour RCC columns for ground floor grid lines 1-6.', 'Masonry Team 2 (Lead: Gamini)', 'URGENT', 'IN_PROGRESS', '2026-04-10'),
(1, 'Electrical Conduit Layout', 'Install PVC conduit pipes across beams before ceiling slab pour.', 'Electrical Crew (Lead: Ruwan)', 'MEDIUM', 'TODO', '2026-04-25');
GO

-- 4. Daily Progress Logs
INSERT INTO daily_progress_logs (project_id, reported_by, log_date, milestone, work_done, issues_faced) VALUES
(1, 'Eng. H. Weerawansha', '2026-03-20', 'Foundation Work Complete', 'Completed column base casting for all 16 perimeter columns.', 'Minor water seepage near boundary wall resolved by pumping.'),
(1, 'Eng. H. Weerawansha', '2026-04-05', 'Ground Floor Columns In Progress', 'Shuttering and reinforcement rebar tied for columns C1 to C8.', 'None. Concrete delivery scheduled for tomorrow.');
GO

-- 5. Materials in Stock
INSERT INTO materials (material_name, category, unit, unit_price, current_stock, reorder_level) VALUES
('Tokyo Super Portland Cement', 'CEMENT', 'Bags', 2350.00, 450, 50),
('Ribbed Tor Steel Bar 16mm', 'STEEL', 'Tons', 345000.00, 15, 3),
('Coarse River Sand', 'SAND', 'Cubes', 18500.00, 28, 5),
('Clay Wire-Cut Bricks', 'BRICKS', 'Units', 38.00, 12500, 2000),
('S-Lon PVC Pipes 1-inch Class 7', 'PLUMBING', 'Meters', 850.00, 180, 40);
GO

-- 6. Material Issues
INSERT INTO material_issues (material_id, project_id, issued_to, quantity_issued, issue_date, notes) VALUES
(1, 1, 'Site Foreman Sunil', 50, '2026-03-15', 'Issued for concrete column footing mix 1:2:4'),
(2, 1, 'Steel Bender Kamal', 4, '2026-03-18', 'Issued for ground floor column cages');
GO

-- 7. Suppliers
INSERT INTO suppliers (company_name, contact_person, phone, email, address, supply_category, status) VALUES
('Lanka Building Materials Ltd', 'Mr. Asoka Wijeratne', '0112345678', 'sales@lankamaterials.lk', 'Peliyagoda, Kelaniya', 'CEMENT, SAND, BRICKS', 'ACTIVE'),
('Melwire Rolling Mills Ltd', 'Mr. Rohan Perera', '0114567890', 'orders@melwire.com', 'Baseline Road, Colombo 09', 'STEEL, WIRE MESH', 'ACTIVE'),
('National Hardware Supplies', 'Mr. D.M. Shantha', '0117890123', 'nationalhw@sltnet.lk', 'Panchikawatta, Colombo 10', 'PLUMBING, ELECTRICAL', 'ACTIVE');
GO

-- 8. Purchase Requests
INSERT INTO purchase_requests (supplier_id, material_name, quantity, estimated_cost, status, request_date, notes) VALUES
(1, 'Tokyo Super Portland Cement', 200, 470000.00, 'APPROVED', '2026-04-01', 'Urgent restock needed before second floor slab pour.'),
(2, 'Ribbed Tor Steel Bar 12mm', 8, 2600000.00, 'PENDING', '2026-04-06', 'Required for beam reinforcement.');
GO

-- 9. Expenses
INSERT INTO expenses (project_id, expense_title, category, amount, expense_date, description) VALUES
(1, 'Excavator Machine Rental (3 Days)', 'EQUIPMENT', 135000.00, '2026-03-05', 'Earth moving and column trenching.'),
(1, 'Ready-Mix Concrete 25 M3', 'MATERIALS', 425000.00, '2026-03-18', 'Poured for column footings Grade 25.'),
(1, 'Weekly Skilled Labor Wages', 'LABOR', 95000.00, '2026-03-22', 'Wages for 5 masons and 6 bar benders.');
GO

-- 10. Quotations
INSERT INTO quotations (project_id, client_name, client_email, project_name, labor_cost, material_cost, overhead_cost, total_amount, status, generated_date) VALUES
(1, 'Nimal Silva', 'client@gmail.com', 'Luxury Villa Project - Rajagiriya', 6500000.00, 14500000.00, 4000000.00, 25000000.00, 'ACCEPTED', '2026-02-15');
GO

-- 11. Inquiries
INSERT INTO inquiries (client_id, client_name, client_email, project_id, subject, message, resolution, status) VALUES
(7, 'Nimal Silva', 'client@gmail.com', 1, 'Inquiry on window frames brand', 'Could you please confirm if the aluminum frames quoted are Alumex powder-coated bronze finish?', 'Yes Mr. Silva, Alumex 100 series powder-coated finish will be used as per contract spec.', 'RESOLVED');
GO
