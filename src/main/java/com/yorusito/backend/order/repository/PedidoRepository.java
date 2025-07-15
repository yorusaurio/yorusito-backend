package com.yorusito.backend.order.repository;

import com.yorusito.backend.order.entity.Pedido;
import com.yorusito.backend.shared.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long usuarioId);
    Page<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long usuarioId, Pageable pageable);
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(EstadoPedido estado);
    Page<Pedido> findAllByOrderByFechaPedidoDesc(Pageable pageable);
}
