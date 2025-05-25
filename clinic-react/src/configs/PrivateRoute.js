import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthProvider";

const PrivateRoute = ({ children, roles }) => {
  const { user, userLoading } = useAuth();
  if (userLoading) return null;
  if (!user) return <Navigate to="/login" />;
  if (roles && !roles.includes(user.userRole)) return <Navigate to="/" />;
  return children;
};

export default PrivateRoute;