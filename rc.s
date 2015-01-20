/*
 * Generated Mon Jan 19 16:06:16 PST 2015
 */

	.section ".rodata"
	.align 4
_endl:	.asciz "\n"
_intFmt:	.asciz "%d"
_strFmt:	.asciz "%s"
_boolT:	.asciz "true"
_boolF:	.asciz "false"
arrrray_error:	.asciz "Index value of %d is outside legal range [0,%d).\n"
del_errrror:	.asciz "Attempt to dereference NULL pointer.\n"

	.section ".data"
	.align 4
.globalInit:	.word 0

! ---.MYS_A_foo---
	.section ".text"
	.align 4
	.global .MYS_A_foo
.MYS_A_foo:
	set			SAVE..MYS_A_foo, %g1
	save		%sp, %g1, %sp

	mov			%fp, %l0
	set			4, %l1
	sub			%l0, %l1, %l1
	st			%i1, [%l1]

	ret
	restore



	SAVE..MYS_A_foo_bool = -(92 + 4) & -8
! ---.MYS_A_foo---
	.section ".text"
	.align 4
	.global .MYS_A_foo
.MYS_A_foo:
	set			SAVE..MYS_A_foo, %g1
	save		%sp, %g1, %sp


	ret
	restore



	SAVE..MYS_A_foo_int = -(92 + 0) & -8
	.section ".data"
	.align 4
x:	.word 0

! ---foo---
	.section ".text"
	.align 4
	.global foo
foo:
	set			SAVE.foo, %g1
	save		%sp, %g1, %sp

	mov			%fp, %l2
	set			4, %l1
	sub			%l2, %l1, %l1
	st			%i0, [%l1]

	ret
	restore



	SAVE.foo_bool = -(92 + 4) & -8
! ---foo---
	.section ".text"
	.align 4
	.global foo
foo:
	set			SAVE.foo, %g1
	save		%sp, %g1, %sp


	ret
	restore



	SAVE.foo_int = -(92 + 0) & -8
! ---main---
	.section ".text"
	.align 4
	.global main
main:
	set			SAVE.main, %g1
	save		%sp, %g1, %sp

	set			.globalInit, %l0
	ld			[%l0], %l0
	cmp			%l0, %g0
	bne			.globalFinish
	nop

!int x
	set			.globalInit, %l0
	set			1, %l1
	st			%l1, [%l0]
.globalFinish:

!int x

!StructType@4e543c44 a : (
	mov			%fp, %l0
	set			8, %o0
	sub			%l0, %o0, %o0
	set			1, %l0
	mov			%fp, %l2
	set			12, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]
!true
	mov			%fp, %l0
	set			8, %o0
	sub			%l0, %o0, %o0
!1
	set			1, %l0
	mov			%fp, %l2
	set			16, %l1
	sub			%l2, %l1, %l1
	st			%l0, [%l1]

	mov			%fp, %l0
	set			8, %o0
	sub			%l0, %o0, %o0
	ret
	restore



	SAVE.main = -(92 + 16) & -8
