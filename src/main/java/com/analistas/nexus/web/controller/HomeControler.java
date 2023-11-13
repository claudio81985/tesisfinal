package com.analistas.nexus.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.analistas.nexus.model.entities.Producto;
import com.analistas.nexus.model.service.ICategoriaService;
import com.analistas.nexus.model.service.IProductoService;

@Controller
public class HomeControler {

    @Autowired
    IProductoService productoService;
    @Autowired
    ICategoriaService categoriaService;

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
    //public String detalleProducto(Model model) {
    public String detalleProducto(@PathVariable("id") Long id, Integer cantidad, Model model) {

        //model.addAttribute("titulo", "Nexus Informatica");

        //Detalle Productos//
        model.addAttribute("titulo", "Descripcion del Producto");
		Producto producto = productoService.buscarPorId(id);
		model.addAttribute("producto", producto);

        return "detalleProducto";
    }



}
