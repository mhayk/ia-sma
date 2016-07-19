/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.io.Serializable;

/**
 *
 * @author mhayk
 */
public class Jogador implements Serializable {

    private String nome;
    private int quantidadePalitos = 3;
    private int PalitosNaMao = -1;
    private String situacao;

    public Jogador(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidadePalitos() {
        return quantidadePalitos;
    }

    public int getPalitosNaMao() {
        return PalitosNaMao;
    }

    public void setPalitosNaMao(int PalitosNaMao) {
        this.PalitosNaMao = PalitosNaMao;
    }
    
    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

}
