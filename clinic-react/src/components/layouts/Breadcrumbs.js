import { Breadcrumb } from "react-bootstrap";
import { Link, useLocation } from "react-router-dom";

const breadcrumbNameMap = {
  "": "Trang chủ",
  "register": "Đăng ký",
  "login": "Đăng nhập",
  "doctors": "Tìm kiếm bác sĩ",
  // Thêm các mapping khác
};

const Breadcrumbs = ({ customTitle }) => {
  const location = useLocation();
  const pathnames = location.pathname.split("/").filter(x => x);

  return (
    <Breadcrumb className="my-3">
      <Breadcrumb.Item linkAs={Link} linkProps={{ to: "/" }}>
        Trang chủ
      </Breadcrumb.Item>
      {pathnames.map((value, idx) => {
        const to = "/" + pathnames.slice(0, idx + 1).join("/");
        const isLast = idx === pathnames.length - 1;
        // Nếu là breadcrumb cuối cùng và có customTitle thì dùng customTitle
        const title = isLast && customTitle
          ? customTitle
          : (breadcrumbNameMap[value] || value);
        return (
          <Breadcrumb.Item
            key={to}
            linkAs={Link}
            linkProps={{ to }}
            active={isLast}
          >
            {title}
          </Breadcrumb.Item>
        );
      })}
    </Breadcrumb>
  );
};

export default Breadcrumbs;