package com.sms.repository;

import com.sms.domain.Student;
import com.sms.util.AppLogger;
import com.sms.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteStudentRepository implements StudentRepository {

    private Connection conn() {
        return DatabaseManager.getInstance().getConnection();
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
            rs.getString("student_id"),
            rs.getString("full_name"),
            rs.getString("programme"),
            rs.getInt("level"),
            rs.getDouble("gpa"),
            rs.getString("email"),
            rs.getString("phone_number"),
            LocalDate.parse(rs.getString("date_added")),
            rs.getString("status")
        );
    }

    @Override
    public void add(Student s) {
        String sql = "INSERT INTO students VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getProgramme());
            ps.setInt(4, s.getLevel());
            ps.setDouble(5, s.getGpa());
            ps.setString(6, s.getEmail());
            ps.setString(7, s.getPhoneNumber());
            ps.setString(8, s.getDateAdded().toString());
            ps.setString(9, s.getStatus());
            ps.executeUpdate();
            AppLogger.info("Added student: " + s.getStudentId());
        } catch (SQLException e) {
            AppLogger.error("Add failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Student s) {
        String sql = "UPDATE students SET full_name=?,programme=?,level=?,gpa=?,email=?,phone_number=?,status=? WHERE student_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getFullName());
            ps.setString(2, s.getProgramme());
            ps.setInt(3, s.getLevel());
            ps.setDouble(4, s.getGpa());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhoneNumber());
            ps.setString(7, s.getStatus());
            ps.setString(8, s.getStudentId());
            ps.executeUpdate();
            AppLogger.info("Updated student: " + s.getStudentId());
        } catch (SQLException e) {
            AppLogger.error("Update failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String studentId) {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.executeUpdate();
            AppLogger.info("Deleted student: " + studentId);
        } catch (SQLException e) {
            AppLogger.error("Delete failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Student> findById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            AppLogger.error("FindById failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            AppLogger.error("FindAll failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Student> search(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            AppLogger.error("Search failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Student> filter(String programme, Integer level, String status) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (programme != null && !programme.isEmpty()) {
            sql.append(" AND programme=?"); params.add(programme);
        }
        if (level != null) {
            sql.append(" AND level=?"); params.add(level);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status=?"); params.add(status);
        }
        try (PreparedStatement ps = conn().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            AppLogger.error("Filter failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean existsById(String studentId) {
        String sql = "SELECT 1 FROM students WHERE student_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            AppLogger.error("ExistsById failed: " + e.getMessage());
        }
        return false;
    }
}
