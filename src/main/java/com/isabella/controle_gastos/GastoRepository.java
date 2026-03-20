package com.isabella.controle_gastos;

import com.isabella.controle_gastos.login.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    // ✅ Queries filtradas por usuário (evita vazamento de dados entre usuários)
    Page<Gasto> findByUsuario(Usuario usuario, Pageable pageable);

    List<Gasto> findByUsuario(Usuario usuario);

    List<Gasto> findByUsuarioAndDataBetween(Usuario usuario, LocalDate inicio, LocalDate fim);

    List<Gasto> findByUsuarioAndCategoriaIgnoreCase(Usuario usuario, String categoria);
}