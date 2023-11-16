package com.analistas.nexus.web.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.analistas.nexus.model.entities.LineaVenta;
import com.analistas.nexus.model.entities.Producto;
import com.analistas.nexus.model.entities.Usuario;
import com.analistas.nexus.model.entities.Venta;
import com.analistas.nexus.model.service.ICategoriaService;
import com.analistas.nexus.model.service.IClienteService;
import com.analistas.nexus.model.service.ILineaVentaService;
import com.analistas.nexus.model.service.IPermisoService;
import com.analistas.nexus.model.service.IProductoService;
import com.analistas.nexus.model.service.IUsuarioService;
import com.analistas.nexus.model.service.IVentaService;

@Controller
public class HomeControler {

    private final Logger log = LoggerFactory.getLogger(HomeControler.class);

    @Autowired
    IProductoService productoService;

    @Autowired
    ICategoriaService categoriaService;

    @Autowired
    IPermisoService permisoService;

    @Autowired
    IVentaService ventaService;

    @Autowired
    ILineaVentaService lineaVentaService;

    @Autowired
    IClienteService clienteService;

    @Autowired
    IUsuarioService UsuarioService; 
    
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

        Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido: {}", optionalProducto.get());
		log.info("Cantidad: {}", cant);
		producto = optionalProducto.get();

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

        venta.setTotal(calcularTotal);
        model.addAttribute("carrito", lineaVenta);
        model.addAttribute("venta", venta);

        return "carrito";
    }

    // Eliminar un producto del carrito
	@GetMapping("/delete/carrito/{id}")
	//@Secured({ "ROLE_ADMIN", "ROLE_CLIENTE" })
	public String deleteProductoCarrito(@PathVariable Long id, Model model) {

		// lista nueva de prodcutos
		List<LineaVenta> ordenesNueva = new ArrayList<LineaVenta>();

		for (LineaVenta lineaVenta : lineaVenta) {
			if (lineaVenta.getProducto().getId() != id) {
				ordenesNueva.add(lineaVenta);

			}
		}

		// Lista con los productos actualizados
		lineaVenta = ordenesNueva;

		double calcularTotal = 0;
		calcularTotal = lineaVenta.stream().mapToDouble(lineas -> lineas.calcularSubtotal()).sum();

		venta.setTotal(calcularTotal);
        model.addAttribute("carrito", lineaVenta);
		model.addAttribute("venta", venta);

		return "Carrito";
	}

    @GetMapping("/resumen")
	//@Secured({ "ROLE_ADMIN", "ROLE_CLIENTE" })
	public String resumen(Model model, HttpSession session, Principal principal) {

		Usuario usuario = ventaService.buscarCajero(principal.getName());

		model.addAttribute("carrito", lineaVenta);
		model.addAttribute("venta", venta);
		model.addAttribute("usuario", usuario);

		return "resumenventa";
	}

	@GetMapping("/guardarVenta")
	//@Secured({ "ROLE_ADMIN", "ROLE_CLIENTE" })
	public String guardarVenta(HttpSession session, RedirectAttributes flash,
			SessionStatus status, Principal principal, @RequestParam(defaultValue = "1") Long idCliente) {

		// venta.setFechaHora(fechaHora);
		venta.setNroFactura(0);

		// usuario
		Usuario usuario = ventaService.buscarCajero(principal.getName());
		venta.setCliente(clienteService.buscarPorId(idCliente));
		venta.setUsuario(usuario);
		ventaService.guardar(venta);

		// guardar lineasVenta
		for (LineaVenta dt : lineaVenta) {
			dt.setVenta(venta);
			lineaVentaService.guardar(dt);

		}

		/// limpiar lista y orden
		venta = new Venta();
		lineaVenta.clear();

		status.setComplete();
		flash.addFlashAttribute("success", "Venta registrada por " + usuario.getNombre());
		return "redirect:/getCompras";
	}

	// OBTENER DETALLES DE COMPRAS/PRODUCTOS
	@GetMapping("/getCompras")
	//@Secured({ "ROLE_ADMIN", "ROLE_CLIENTE" })
	public String getCompras(Model model, HttpSession session, Principal principal) {

		// model.addAttribute("sesion", session.getAttribute("id_usuario"));
		// usuarioService.buscarPorId(Long.parseLong(session.getAttribute("id_usuario").toString()));

		Usuario usuario = ventaService.buscarCajero(principal.getName());
		List<Venta> compras = ventaService.findByUsuario(usuario);
       
		model.addAttribute("compras", compras);

		return "compras";
	}

	@GetMapping("/getDetalle/{id}")
	//@Secured({ "ROLE_ADMIN", "ROLE_CLIENTE" })
	public String getDetalle(Model model, @PathVariable Long id) {
		log.info("Id de la orden {}", id);
		Venta venta = ventaService.buscarPorId(id);

		model.addAttribute("detalles", venta.getLineas());

		return "detallecompra";
	}


}
