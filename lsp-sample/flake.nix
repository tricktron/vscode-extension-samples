{
  description = "try-purescript-dev-shell";
  inputs.flake-utils.url = "github:numtide/flake-utils";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs";

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachSystem [ "x86_64-darwin" "x86_64-linux" ] (system:
      let
        pkgs = import nixpkgs {
          inherit system;
        };
      in
      {
        devShell = pkgs.mkShell {
          buildInputs = with pkgs; [
            nodejs-12_x
            gradle
          ];
        };
      });
}
