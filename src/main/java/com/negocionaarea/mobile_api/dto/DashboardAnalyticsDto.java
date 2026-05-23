package com.negocionaarea.mobile_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardAnalyticsDto {

    private Long totalCurtidas;
    private Long curtidasMesAtual;
    private Long curtidasMesAnterior;
    private Double variacaoPercentual;
    private String publicacaoMaisCurtida;
    private Long curtidasPublicacaoTop;
    private Double mediaPorPublicacao;

    private List<CurtidasPorDiaDto> curtidasPorDia;
    private List<CurtidasPorBairroDto> curtidasPorBairro;
    private List<CurtidasPorHoraDto> curtidasPorHora;
    private List<CurtidasPorDiaSemanDto> curtidasPorDiaSemana;
    private List<TopProdutoDto> topProdutos;

    public static class CurtidasPorDiaDto {
        private String data;   // "yyyy-MM-dd"
        private Long total;

        public CurtidasPorDiaDto(String data, Long total) {
            this.data = data;
            this.total = total;
        }
        public String getData()  { return data;  }
        public Long   getTotal() { return total; }
    }

    public static class CurtidasPorBairroDto {
        private String bairro;
        private Long total;

        public CurtidasPorBairroDto(String bairro, Long total) {
            this.bairro = bairro;
            this.total  = total;
        }
        public String getBairro() { return bairro; }
        public Long   getTotal()  { return total;  }
    }

    public static class CurtidasPorHoraDto {
        private Integer hora;
        private Long total;

        public CurtidasPorHoraDto(Integer hora, Long total) {
            this.hora  = hora;
            this.total = total;
        }
        public Integer getHora()  { return hora;  }
        public Long    getTotal() { return total; }
    }

    public static class CurtidasPorDiaSemanDto {
        private String diaSemana;  // "SEG", "TER", etc.
        private Long total;

        public CurtidasPorDiaSemanDto(String diaSemana, Long total) {
            this.diaSemana = diaSemana;
            this.total     = total;
        }
        public String getDiaSemana() { return diaSemana; }
        public Long   getTotal()     { return total;     }
    }

    public static class TopProdutoDto {
        private String nome;
        private Long curtidas;
        private String imagem;

        public TopProdutoDto(String nome, Long curtidas, String imagem) {
            this.nome     = nome;
            this.curtidas = curtidas;
            this.imagem   = imagem;
        }
        public String getNome()     { return nome;     }
        public Long   getCurtidas() { return curtidas; }
        public String getImagem()   { return imagem;   }
    }
}
