# -*- mode: ruby -*-
# vi: set ft=ruby :

IP_ADDRESS = ENV["VAGRANT_IP_ADDRESS"] || "10.142.142.10"
VM_RAM = ENV["VAGRANT_VM_RAM"] || 2048
VM_CPUS = ENV["VAGRANT_VM_CPUS"] || 2

Vagrant.configure("2") do |config|

  config.vm.box = "stigsb/playa-mesos"

  config.vm.network "private_network", ip: IP_ADDRESS

  config.vm.provider :virtualbox do |vb|
    vb.memory = VM_RAM
    vb.cpus = VM_CPUS
  end

end
