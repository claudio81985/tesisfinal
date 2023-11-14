package com.analistas.nexus.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.analistas.nexus.model.entities.LineaVenta;
import com.analistas.nexus.model.entities.Producto;
import com.analistas.nexus.model.entities.Venta;
import com.analistas.nexus.model.service.ICategoriaService;
import com.analistas.nexus.model.service.IProductoService;

@Controller
public class HomeControler {

	private final Logger log = LoggerFactory.getLogger(HomeControler.class);

    @Autowired
    IProductoService productoService;
    @Autowired
    ICategoriaService categoriaService;   

    // Para almacenar las lineas de la venta Online
    List<LineaVenta> lineaVenta = new ArrayList<LineaVenta>();

    // Datos de la venta Online
    Venta venta = new Venta();

    @GetMapping({ "/", "/home" })
    public String home(Model model) {

        model.addAttribute("titulo", "Nexus Informatica");
        model.addAttribute("productos", productoService.buscarTodo());
        model.addAttribute("categorias", categoriaService.buscarTodo());

        return "home";
    }

    @GetMapping("/contacto")
    public String contacto(Model model) {

        model.addAttribute("titulo", "Nexus Informatica");

        return "contacto";
    }

    @GetMapping("/conocenos")
    public String conocenos(Model model) {

        model.addAttribute("titulo", "Nexus Informatica");

        return "conocenos";
    }

    @GetMapping("/detalleProducto/{id}")
    // public String detalleProducto(Model model) {
    public String detalleProducto(@PathVariable("id") Long id, Integer cantidad, Model model) {

        // model.addAttribute("titulo", "Nexus Informatica");

        // Detalle Productos//
        model.addAttribute("titulo", "Descripcion del Producto");
        Producto producto = productoService.buscarPorId(id);
        model.addAttribute("producto", producto);

        return "detalleProducto";
    }

    // Vista Productos agregados al carrito - Datos de la venta Online
    @PostMapping("/carrito")
    // @Secured({ "ROLE_ADMIN", "ROLE_CLIENTE" })
    public String addCarrito(@RequestParam Long id, @RequestParam Integer cant,
            Model model) {

        model.addAttribute("titulo", "Carrito de Compras");
        LineaVenta linea = new LineaVenta();
        Producto producto = new Producto();
        double calcularTotal = 0;

        //Optional<Producto> optionalProducto = ProductoServiceImpl.get(id);
        //log.info("Producto añadido: {}", optionalProducto.get());
        //log.info("Cantidad: {}", cant);
        //producto = optionalProducto.get();

        linea.setCantidad(cant);
        linea.setPrecioActual(producto.getPrecio());
        linea.setProducto(producto);

        // validar que le producto no se añada 2 veces
        // Long idproducto = producto.getId();
        // boolean ingresado = lineaVenta.stream().anyMatch(prod ->
        // prod.getProducto().getId() == idproducto);

        // if (!ingresado) {
        // Agregar linea a carrito
        // lineaVenta.add(linea);
        // }
        // else {
        // linea.setCantidad(linea.getCantidad() + cant);
        // lineaVentaService.guardar(linea);
        // log.info("Producto Repetido: {}", optionalProducto.get());
        // log.info("Cantidad: {}", cant);
        // }
        lineaVenta.add(linea);

        // linea.setCantidad(linea.getCantidad() + cant);
        // linea.setCantidad(cant);

        // Actualizar stock de producto:
        producto.setStock(producto.getStock() - cant);
        productoService.guardar(producto);
        log.info("Producto Stock: {}", producto.getStock());

        // Calcular total de la venta
        calcularTotal = lineaVenta.stream().mapToDouble(lineas -> lineas.calcularSubtotal()).sum();

        Venta.setTotal(calcularTotal);
        model.addAttribute("carrito", lineaVenta);
        model.addAttribute("venta", venta);

        return "carrito";
    }

}
