import axios from "axios";

export const fetchProjects = async (type) => {
  const endpointMap = {
    relevant: "/api/student/projects/relevant",     // expects: roles (List<String>)
    available: "/api/student/projects/available",   // no params
    enrolled: "/api/student/projects/enrolled",     // expects: userId (UUID)
    completed: "/api/student/projects/completed",   // expects: userId (UUID)
    certificates: "/api/student/certificates"       // TBD
  };

  const endpoint = endpointMap[type];
  if (!endpoint) {
    console.warn(`Unknown project type: ${type}`);
    return [];
  }

  const url = `http://localhost:8080${endpoint}`;
  let params = {};
  const storedUser = localStorage.getItem("user");

  if (type === "relevant") {
    if (storedUser) {
      try {
        const parsed = JSON.parse(storedUser);
        const userRoles = parsed.rolesLookingFor || parsed.roles;
        console.log("[fetchProjects] Relevant roles:", userRoles);
        if (Array.isArray(userRoles) && userRoles.length > 0) {
          params.roles = userRoles;
        } else {
          params.roles = [];
        }
      } catch (e) {
        console.warn("Failed to parse user roles from localStorage:", e);
        params.roles = [];
      }
    } else {
      console.log("[fetchProjects] No stored user, sending empty roles");
      params.roles = [];
    }
  }

  if (["enrolled", "completed"].includes(type)) {
    if (storedUser) {
      try {
        const parsed = JSON.parse(storedUser);
        console.log(`[fetchProjects] ${type} userId:`, parsed.id);
        if (parsed.id) {
          params.userId = parsed.id;
        } else {
          console.warn("User ID not found in localStorage");
          return [];
        }
      } catch (e) {
        console.warn("Failed to parse user from localStorage:", e);
        return [];
      }
    } else {
      console.warn("No user found in localStorage");
      return [];
    }
  }

  try {
    console.log(`Fetching ${type} projects from ${url} with params:`, params);

    const config = {
      withCredentials: true,
      params
    };

    if (type === "relevant" && Array.isArray(params.roles)) {
      config.paramsSerializer = (params) => {
        const parts = [];
        for (const key in params) {
          if (Array.isArray(params[key])) {
            if (params[key].length > 0) {
              params[key].forEach((value) => {
                parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
              });
            }
          } else if (params[key] != null) {
            parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`);
          }
        }
        return parts.join("&");
      };
    }

    const response = await axios.get(url, config);
    console.log(`[fetchProjects] ${type} response:`, response.data);
    return Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    const status = error.response?.status;
    const message = error.response?.data?.message;
    if (status === 401 && message === "TOKEN_MISSING") {
      console.warn("Auth token missing. Redirecting to login...");
      window.location.href = "/student/login";
      return [];
      // stop further handling
    } 
    console.error(`Failed to fetch ${type} projects:`, error);
    return [];
  }
};

export const fetchAllProjects = async (activeSection) => {

  const enrolled = await fetchProjects("enrolled");

  const enrolledIds = new Set(enrolled.map(p => p.id));

  const data = await fetchProjects(activeSection);

  const marked = data.map(p => {
    const projectId = p.id || p.projectId; // normalize if backend uses projectId
    if (!projectId) {
      console.warn("[fetchAllProjects] Project missing id:", p);
    }
    return {
      ...p,
      id: projectId,
      isEnrolled: enrolledIds.has(projectId),
    };
  });

  console.log(`[fetchAllProjects] Marked ${activeSection} projects:`, marked);
  return marked;
};

export const enrollProject = async (userId, projectId, startDate) => {
  try {
    if (!userId) {
      const storedUser = localStorage.getItem("user");
      userId = storedUser ? JSON.parse(storedUser).id : null;
    }
    if (!projectId) throw new Error("Project ID is required");
    if (!startDate) throw new Error("Start date is required");

    const response = await axios.post(
      "http://localhost:8080/api/student/projects/enroll",
      null,
      {
        withCredentials: true,
        params: { userId, projectId, startDate }
      }
    );
    return response.data;
  } catch (error) {
    const status = error.response?.status;
    const message = error.response?.data?.message;
    if (status === 401 && message === "TOKEN_MISSING") {
      window.location.href = "student/login";
      return [];
    }
    console.error("Enrollment failed:", error);
    return [];
  }
};


export const unenrollProject = async (userId, projectId) => {
  try {
    if (!userId) {
      const storedUser = localStorage.getItem("user");
      userId = storedUser ? JSON.parse(storedUser).id : null;
    }
    console.log("[unenrollProject] userId:", userId, "projectId:", projectId);

    if (!projectId) throw new Error("Project ID is required");

    const response = await axios.delete(
      "http://localhost:8080/api/student/projects/unenroll",
      {
        withCredentials: true,
        params: { userId, projectId }
      }
    );
    console.log("[unenrollProject] Response:", response.data);
    return response.data;
  } catch (error) {
    const status = error.response?.status;
    const message = error.response?.data?.message;
    if (status === 401 && message === "TOKEN_MISSING") {
      console.warn("Auth token missing. Redirecting to login...");
      window.location.href = "/student/login";
      return [];
      // stop further handling
    } 
    console.error(`Failed to fetch ${type} projects:`, error);
    return [];
  }
};
