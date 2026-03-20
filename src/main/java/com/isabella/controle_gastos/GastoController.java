package com.isabella.controle_gastos;

import com.isabella.controle_gastos.login.Usuario;
import com.isabella.controle_gastos.login.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    private final GastoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public GastoController(GastoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioLogado(HttpServletRequest request) {
        String username = (String) request.getAttribute("usuario");
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        }
        return usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private String escaparCsv(String valor) {
        if (valor == null) return "";
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }

    @GetMapping
    public Page<Gasto> listar(Pageable pageable, HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        return repository.findByUsuario(usuario, pageable);
    }

    @PostMapping
    public Gasto criar(@RequestBody Gasto gasto, HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        gasto.setUsuario(usuario);
        gasto.setData(LocalDate.now());
        return repository.save(gasto);
    }

    @GetMapping("/por-mes")
    public List<Gasto> buscarPorMes(
            @RequestParam int mes,
            @RequestParam int ano,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        return repository.findByUsuarioAndDataBetween(usuario, inicio, fim);
    }

    @GetMapping("/total-por-mes")
    public double totalPorMes(
            @RequestParam int mes,
            @RequestParam int ano,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        return repository.findByUsuarioAndDataBetween(usuario, inicio, fim)
                .stream()
                .mapToDouble(Gasto::getValor)
                .sum();
    }

    // ✅ CSV com BOM UTF-8 e byte[] para Excel reconhecer acentos
    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCSV(HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        List<Gasto> gastos = repository.findByUsuario(usuario);

        StringBuilder csv = new StringBuilder("\uFEFF"); // BOM UTF-8
        csv.append("id,descricao,valor,categoria,data,mes,ano\n");

        for (Gasto g : gastos) {
            csv.append(g.getId()).append(",");
            csv.append(escaparCsv(g.getDescricao())).append(",");
            csv.append(g.getValor()).append(",");
            csv.append(escaparCsv(g.getCategoria())).append(",");
            csv.append(g.getData()).append(",");
            if (g.getData() != null) {
                csv.append(g.getData().getMonthValue()).append(",");
                csv.append(g.getData().getYear());
            } else {
                csv.append(",");
            }
            csv.append("\n");
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=gastos_powerbi.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(bytes);
    }

    @GetMapping("/por-periodo")
    public List<Gasto> buscarPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);
        return repository.findByUsuarioAndDataBetween(usuario, dataInicio, dataFim);
    }

    @GetMapping("/media")
    public double mediaGastos(HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        return repository.findByUsuario(usuario)
                .stream()
                .mapToDouble(Gasto::getValor)
                .average()
                .orElse(0.0);
    }

    @GetMapping("/maior")
    public Gasto maiorGasto(HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        return repository.findByUsuario(usuario)
                .stream()
                .max((g1, g2) -> Double.compare(g1.getValor(), g2.getValor()))
                .orElse(null);
    }

    @GetMapping("/menor")
    public Gasto menorGasto(HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        return repository.findByUsuario(usuario)
                .stream()
                .min((g1, g2) -> Double.compare(g1.getValor(), g2.getValor()))
                .orElse(null);
    }

    @GetMapping("/alerta")
    public String alerta(
            @RequestParam(defaultValue = "1000") double limite,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        double total = repository.findByUsuario(usuario)
                .stream()
                .mapToDouble(Gasto::getValor)
                .sum();

        if (total > limite) {
            return String.format("Você gastou mais de R$%.2f! Total: R$%.2f", limite, total);
        }
        return String.format("Seus gastos estão sob controle. Total: R$%.2f", total);
    }

    // ✅ CSV por mês com BOM UTF-8 e byte[] para Excel reconhecer acentos
    @GetMapping("/exportar-csv-por-mes")
    public ResponseEntity<byte[]> exportarCSVPorMes(
            @RequestParam int mes,
            @RequestParam int ano,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());

        List<Gasto> gastos = repository.findByUsuarioAndDataBetween(usuario, inicio, fim);

        StringBuilder csv = new StringBuilder("\uFEFF"); // BOM UTF-8
        csv.append("id,descricao,valor,categoria,data\n");

        for (Gasto g : gastos) {
            csv.append(g.getId()).append(",");
            csv.append(escaparCsv(g.getDescricao())).append(",");
            csv.append(g.getValor()).append(",");
            csv.append(escaparCsv(g.getCategoria())).append(",");
            csv.append(g.getData()).append("\n");
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=gastos_mes.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(bytes);
    }

    @GetMapping("/total-por-categoria")
    public Map<String, Double> totalPorCategoria(HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        List<Gasto> gastos = repository.findByUsuario(usuario);

        Map<String, Double> totais = new HashMap<>();
        for (Gasto gasto : gastos) {
            String categoria = gasto.getCategoria();
            if (categoria == null || categoria.isBlank()) {
                categoria = "Outros";
            }
            totais.merge(categoria, gasto.getValor(), Double::sum);
        }
        return totais;
    }

    @GetMapping("/por-categoria")
    public List<Gasto> buscarPorCategoria(
            @RequestParam String categoria,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        return repository.findByUsuarioAndCategoriaIgnoreCase(usuario, categoria);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id, HttpServletRequest request) {
        Usuario usuario = getUsuarioLogado(request);
        Gasto gasto = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto não encontrado"));

        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para deletar este gasto");
        }
        repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Gasto atualizar(
            @PathVariable Long id,
            @RequestBody Gasto gastoAtualizado,
            HttpServletRequest request
    ) {
        Usuario usuario = getUsuarioLogado(request);
        Gasto gasto = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gasto não encontrado"));

        if (!gasto.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para editar este gasto");
        }

        gasto.setDescricao(gastoAtualizado.getDescricao());
        gasto.setValor(gastoAtualizado.getValor());
        gasto.setCategoria(gastoAtualizado.getCategoria());
        gasto.setData(gastoAtualizado.getData());

        return repository.save(gasto);
    }
}