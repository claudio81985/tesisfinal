package com.analistas.nexus.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.analistas.nexus.model.entities.Usuario;
import com.analistas.nexus.model.entities.Venta;
import com.analistas.nexus.model.repository.IVentaRepositoy;
import com.analistas.nexus.model.repository.UsuarioRepository;


import javax.transaction.Transactional;

@Service
public class IVentaServiceImpl implements IVentaService {

 
    @Autowired
    IVentaRepositoy ventaRepository;

    @Autowired
    UsuarioRepository usuarioRepo;

    @Override
    @Transactional
    public List<Venta> buscarTodo() {
        
        return ventaRepository.findAll();
    }

    @Override
    @Transactional
    public Venta buscarPorId(Long id) {
        
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Venta venta) {
        ventaRepository.save(venta);        
    }

     @Override
    @Transactional
    public Usuario buscarCajero(String nombre) {

        return usuarioRepo.findByNombre(nombre);
    }

    @Override
    public List<Venta> findByUsuario(Usuario usuario) {

        return ventaRepository.findByUsuario(usuario);
    }

}
