// EduElevate LMS - Comprehensive API Testing Script
// One-click testing - just run: node test-api.js

const https = require("https");
const http = require("http");

const baseUrl = "http://localhost:8080/api";
const timestamp = Date.now();

// Test results storage
let results = [];
let tokens = { admin: "", instructor: "", student: "" };
let ids = {
  admin: "",
  instructor: "",
  student: "",
  course1: "",
  course2: "",
};

// Colors for console output
const colors = {
  reset: "\x1b[0m",
  green: "\x1b[32m",
  red: "\x1b[31m",
  yellow: "\x1b[33m",
  cyan: "\x1b[36m",
  magenta: "\x1b[35m",
};

function log(message, color = "reset") {
  console.log(colors[color] + message + colors.reset);
}

// HTTP request helper
function makeRequest(method, endpoint, data = null, headers = {}) {
  return new Promise((resolve, reject) => {
    const url = baseUrl + endpoint;
    const options = {
      method: method,
      headers: {
        "Content-Type": "application/json",
        ...headers,
      },
    };

    const req = http.request(url, options, (res) => {
      let responseData = "";

      res.on("data", (chunk) => {
        responseData += chunk;
      });

      res.on("end", () => {
        try {
          const parsedData = responseData ? JSON.parse(responseData) : null;
          resolve({
            statusCode: res.statusCode,
            data: parsedData,
            headers: res.headers,
          });
        } catch (e) {
          resolve({
            statusCode: res.statusCode,
            data: responseData,
            headers: res.headers,
          });
        }
      });
    });

    req.on("error", (err) => {
      reject(err);
    });

    if (data) {
      req.write(JSON.stringify(data));
    }

    req.end();
  });
}

// Test runner
async function runTest(
  name,
  method,
  endpoint,
  expectedCode,
  data = null,
  headers = {}
) {
  try {
    log(`Testing: ${method} ${endpoint} - ${name}`, "cyan");

    const response = await makeRequest(method, endpoint, data, headers);
    const success = response.statusCode === expectedCode;

    const result = {
      name,
      method,
      endpoint,
      expectedCode,
      actualCode: response.statusCode,
      success,
      data: response.data,
      error: null,
    };

    results.push(result);

    if (success) {
      log(`✅ PASS [${response.statusCode}]`, "green");
    } else {
      log(`❌ FAIL [${response.statusCode}] Expected: ${expectedCode}`, "red");
    }

    return result;
  } catch (error) {
    const result = {
      name,
      method,
      endpoint,
      expectedCode,
      actualCode: "ERROR",
      success: false,
      data: null,
      error: error.message,
    };

    results.push(result);
    log(`❌ ERROR: ${error.message}`, "red");
    return result;
  }
}

// Main testing function
async function runAllTests() {
  log("🚀 Starting EduElevate LMS API Testing...", "green");
  log("=".repeat(50), "yellow");

  try {
    // PHASE 1: Authentication
    log("\n🔐 PHASE 1: Authentication Tests", "magenta");

    // Admin Registration
    const adminData = {
      username: `admin.test.${timestamp}`,
      password: "AdminPass123!",
      email: `admin.test.${timestamp}@eduelevate.com`,
      firstName: "Test",
      lastName: "Admin",
      userType: "admin",
    };

    let result = await runTest(
      "Admin Registration",
      "POST",
      "/auth/register",
      200,
      adminData
    );
    if (result.success && result.data) {
      tokens.admin = result.data.token;
      ids.admin = result.data.userId;
    }

    // Admin Login (backup)
    const adminLogin = {
      username: `admin.test.${timestamp}`,
      password: "AdminPass123!",
      userType: "admin",
    };

    result = await runTest(
      "Admin Login",
      "POST",
      "/auth/login",
      200,
      adminLogin
    );
    if (result.success && result.data) {
      tokens.admin = result.data.token;
      ids.admin = result.data.userId;
    }

    // PHASE 2: Instructor Management
    log("\n👨‍🏫 PHASE 2: Instructor Management", "magenta");

    if (tokens.admin) {
      const instructorData = {
        username: `instructor.test.${timestamp}`,
        password: "InstructorPass123!",
        email: `instructor.test.${timestamp}@eduelevate.com`,
        firstName: "Test",
        lastName: "Instructor",
        department: "Computer Science",
        specialization: "Software Engineering",
      };

      const headers = { Authorization: `Bearer ${tokens.admin}` };
      result = await runTest(
        "Create Instructor",
        "POST",
        "/instructors",
        201,
        instructorData,
        headers
      );
      if (result.success && result.data) {
        ids.instructor = result.data.instructorId;
      }
    }

    // Instructor Login
    const instructorLogin = {
      username: `instructor.test.${timestamp}`,
      password: "InstructorPass123!",
      userType: "instructor",
    };

    result = await runTest(
      "Instructor Login",
      "POST",
      "/auth/login",
      200,
      instructorLogin
    );
    if (result.success && result.data) {
      tokens.instructor = result.data.token;
    }

    // PHASE 3: Student Management
    log("\n🎓 PHASE 3: Student Management", "magenta");

    if (tokens.admin) {
      const studentData = {
        username: `student.test.${timestamp}`,
        password: "StudentPass123!",
        email: `student.test.${timestamp}@eduelevate.com`,
        firstName: "Test",
        lastName: "Student",
        major: "Computer Science",
        year: 2,
      };

      const headers = { Authorization: `Bearer ${tokens.admin}` };
      result = await runTest(
        "Create Student",
        "POST",
        "/students",
        201,
        studentData,
        headers
      );
      if (result.success && result.data) {
        ids.student = result.data.studentId;
      }
    }

    // Student Login
    const studentLogin = {
      username: `student.test.${timestamp}`,
      password: "StudentPass123!",
      userType: "student",
    };

    result = await runTest(
      "Student Login",
      "POST",
      "/auth/login",
      200,
      studentLogin
    );
    if (result.success && result.data) {
      tokens.student = result.data.token;
    }

    // PHASE 4: Admin Endpoints
    log("\n👤 PHASE 4: Admin Endpoints", "magenta");

    if (tokens.admin) {
      const headers = { Authorization: `Bearer ${tokens.admin}` };

      await runTest("Get All Admins", "GET", "/admins", 200, null, headers);
      await runTest(
        "Get Admin by ID",
        "GET",
        `/admins/${ids.admin}`,
        200,
        null,
        headers
      );
      await runTest(
        "Admin: Get All Students",
        "GET",
        "/admin/students",
        200,
        null,
        headers
      );
      await runTest(
        "Admin: Get All Instructors",
        "GET",
        "/admin/instructors",
        200,
        null,
        headers
      );
    }

    // PHASE 5: Course Management
    log("\n📚 PHASE 5: Course Management", "magenta");

    if (tokens.instructor && ids.instructor) {
      const headers = { Authorization: `Bearer ${tokens.instructor}` };

      // Create Course 1
      const course1Data = {
        title: "Data Structures and Algorithms",
        description: "Comprehensive course on data structures",
        capacity: 50,
        durationWeeks: 16,
        instructorId: parseInt(ids.instructor),
      };

      result = await runTest(
        "Create Course 1",
        "POST",
        "/courses",
        201,
        course1Data,
        headers
      );
      if (result.success && result.data) {
        ids.course1 = result.data.courseId;
      }

      // Create Course 2
      const course2Data = {
        title: "Web Development Bootcamp",
        description: "Full-stack web development",
        capacity: 30,
        durationWeeks: 12,
        instructorId: parseInt(ids.instructor),
      };

      result = await runTest(
        "Create Course 2",
        "POST",
        "/courses",
        201,
        course2Data,
        headers
      );
      if (result.success && result.data) {
        ids.course2 = result.data.courseId;
      }
    }

    // Course Read Operations
    await runTest("Get All Courses", "GET", "/courses", 200);
    await runTest(
      "Get All Courses (Non-paginated)",
      "GET",
      "/courses?paginated=false",
      200
    );

    if (ids.course1) {
      await runTest("Get Course by ID", "GET", `/courses/${ids.course1}`, 200);
    }

    await runTest(
      "Search Courses by Title",
      "GET",
      "/courses/search?title=Data",
      200
    );

    if (ids.instructor) {
      await runTest(
        "Get Courses by Instructor",
        "GET",
        `/courses/instructor/${ids.instructor}`,
        200
      );
    }

    // PHASE 6: Enrollment Tests
    log("\n📝 PHASE 6: Enrollment Tests", "magenta");

    if (ids.course1 && ids.student && tokens.student) {
      const headers = { Authorization: `Bearer ${tokens.student}` };

      await runTest(
        "Enroll Student in Course",
        "POST",
        `/courses/${ids.course1}/enroll?studentId=${ids.student}`,
        201,
        null,
        headers
      );
      await runTest(
        "Check Enrollment Status",
        "GET",
        `/courses/${ids.course1}/students/${ids.student}/enrolled`,
        200,
        null,
        headers
      );
      await runTest(
        "Get Student Courses",
        "GET",
        `/courses/student/${ids.student}`,
        200,
        null,
        headers
      );
    }

    if (ids.course1 && tokens.instructor) {
      const headers = { Authorization: `Bearer ${tokens.instructor}` };

      await runTest(
        "Get Course Enrollments",
        "GET",
        `/courses/${ids.course1}/enrollments`,
        200,
        null,
        headers
      );
    }

    if (ids.course1) {
      await runTest(
        "Get Course Statistics",
        "GET",
        `/courses/${ids.course1}/stats`,
        200
      );
    }

    // PHASE 7: Update & Delete Tests
    log("\n✏️ PHASE 7: Update & Delete Tests", "magenta");

    if (ids.course1 && tokens.instructor) {
      const headers = { Authorization: `Bearer ${tokens.instructor}` };

      const updateData = {
        title: "Advanced Data Structures and Algorithms",
        description: "Updated comprehensive course on data structures",
        capacity: 60,
        durationWeeks: 18,
      };

      await runTest(
        "Update Course",
        "PUT",
        `/courses/${ids.course1}`,
        200,
        updateData,
        headers
      );
    }

    if (ids.course2 && tokens.admin) {
      const headers = { Authorization: `Bearer ${tokens.admin}` };
      await runTest(
        "Delete Course (Admin)",
        "DELETE",
        `/courses/${ids.course2}`,
        204,
        null,
        headers
      );
    }
  } catch (error) {
    log(`💥 FATAL ERROR: ${error.message}`, "red");
  }

  // Generate Report
  log("\n📊 Generating Final Report...", "magenta");
  generateReport();
}

function generateReport() {
  const total = results.length;
  const passed = results.filter((r) => r.success).length;
  const failed = total - passed;

  log("\n🎯 TESTING COMPLETE!", "green");
  log(`📈 Results: ${passed}/${total} passed (${failed} failed)`, "yellow");

  if (failed > 0) {
    log("\n❌ Failed Tests:", "red");
    results
      .filter((r) => !r.success)
      .forEach((result) => {
        log(
          `  • ${result.method} ${result.endpoint} - ${result.name} [${result.actualCode}]`,
          "red"
        );
        if (result.error) {
          log(`    Error: ${result.error}`, "red");
        }
      });
  }

  log("\n✅ Successful Tests:", "green");
  results
    .filter((r) => r.success)
    .forEach((result) => {
      log(
        `  • ${result.method} ${result.endpoint} - ${result.name} [${result.actualCode}]`,
        "green"
      );
    });

  // Save detailed report
  const reportFile = `endpoint-test-report-${timestamp}.json`;
  const fs = require("fs");

  const report = {
    timestamp: new Date().toISOString(),
    summary: { total, passed, failed },
    tokens,
    ids,
    results,
  };

  fs.writeFileSync(reportFile, JSON.stringify(report, null, 2));
  log(`\n📋 Detailed report saved to: ${reportFile}`, "cyan");
}

// Run the tests
runAllTests().catch((error) => {
  log(`💥 UNHANDLED ERROR: ${error.message}`, "red");
  process.exit(1);
});
