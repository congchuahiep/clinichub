import { createContext, useContext, useEffect, useState, useCallback } from "react";
import cookie from "react-cookies";
import { authApis, endpoints } from "../configs/APIs";
import { useNavigate } from "react-router-dom";

export const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [userLoading, setUserLoading] = useState(true);

  // Kiểm tra token và load user khi app khởi động
  useEffect(() => {
    const checkUser = async () => {
      const token = cookie.load("token");
      if (token) {
        try {
          const res = await authApis().get(endpoints["current-user"]);
          setUser(res.data);
        } catch {
          cookie.remove("token");
          setUser(null);
        }
      }
      setUserLoading(false);
    };
    checkUser();
  }, []);

  // Đăng nhập
  const login = useCallback(async (username, password) => {
    const res = await authApis().post(endpoints["login"], { username, password });
    cookie.save("token", res.data.token, { path: "/" });
    const userRes = await authApis().get(endpoints["current-user"]);
    setUser(userRes.data);
    return userRes.data;
  }, []);

  // Đăng xuất
  const logout = useCallback(() => {
    cookie.remove("token", { path: "/", domain: window.location.hostname });
    setUser(null);
    navigate("/login");
  }, []);

  return (
    <AuthContext.Provider value={{ user, userLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};